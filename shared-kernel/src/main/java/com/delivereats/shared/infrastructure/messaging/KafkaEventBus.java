package com.delivereats.shared.infrastructure.messaging;

import com.delivereats.shared.infrastructure.serialization.EventSerializer;
import com.delivereats.shared.infrastructure.serialization.JsonEventSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KafkaEventBus implements EventBus {

    private static final Logger LOG = Logger.getLogger(KafkaEventBus.class.getName());

    private final KafkaProducer<String, String> producer;
    private final EventSerializer serializer;
    private final String bootstrapServers;
    private final ExecutorService executorService;
    private final List<KafkaConsumer<String, String>> consumers;
    private volatile boolean running = true;

    public KafkaEventBus() {
        this.serializer = new JsonEventSerializer();
        this.bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092");
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        this.consumers = new CopyOnWriteArrayList<>();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void publish(String topic, Object event) {
        String json = serializer.serialize(event);
        producer.send(new ProducerRecord<>(topic, json), (metadata, exception) -> {
            if (exception != null) {
                LOG.log(Level.SEVERE, "Failed to publish event to topic " + topic, exception);
            } else {
                LOG.info("Published event to " + topic + " [partition=" + metadata.partition() + ", offset=" + metadata.offset() + "]");
            }
        });
    }

    @Override
    public <T> void subscribe(String topic, Class<T> eventType, EventHandler<T> handler, String consumerGroup) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        consumers.add(consumer);

        executorService.submit(() -> {
            try {
                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    records.forEach(record -> {
                        try {
                            T event = serializer.deserialize(record.value(), eventType);
                            handler.handle(event);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Failed to process record from topic " + topic + ": " + e.getMessage(), e);
                        }
                    });
                }
            } catch (Exception e) {
                if (running) {
                    LOG.log(Level.SEVERE, "Consumer loop error for topic " + topic, e);
                }
            } finally {
                consumer.close();
            }
        });
    }

    @Override
    public void close() {
        running = false;
        producer.close();
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
