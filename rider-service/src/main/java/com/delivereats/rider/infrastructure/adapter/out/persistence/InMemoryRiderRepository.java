package com.delivereats.rider.infrastructure.adapter.out.persistence;

import com.delivereats.rider.domain.model.Rider;
import com.delivereats.rider.domain.model.RiderStatus;
import com.delivereats.rider.domain.port.out.RiderRepositoryPort;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRiderRepository implements RiderRepositoryPort {

    private final ConcurrentHashMap<String, Rider> riders = new ConcurrentHashMap<>();

    public InMemoryRiderRepository() {
        Rider r1 = new Rider(UUID.randomUUID().toString(), "Carlos García", RiderStatus.AVAILABLE, "Centro");
        Rider r2 = new Rider(UUID.randomUUID().toString(), "María López", RiderStatus.AVAILABLE, "Norte");
        Rider r3 = new Rider(UUID.randomUUID().toString(), "Juan Hernández", RiderStatus.AVAILABLE, "Sur");
        Rider r4 = new Rider(UUID.randomUUID().toString(), "Ana Martínez", RiderStatus.AVAILABLE, "Poniente");
        Rider r5 = new Rider(UUID.randomUUID().toString(), "Pedro Sánchez", RiderStatus.AVAILABLE, "Oriente");

        riders.put(r1.getId(), r1);
        riders.put(r2.getId(), r2);
        riders.put(r3.getId(), r3);
        riders.put(r4.getId(), r4);
        riders.put(r5.getId(), r5);
    }

    @Override
    public void save(Rider rider) {
        riders.put(rider.getId(), rider);
    }

    @Override
    public Optional<Rider> findAvailable() {
        return riders.values().stream()
                .filter(r -> r.getStatus() == RiderStatus.AVAILABLE)
                .findFirst();
    }

    @Override
    public void updateStatus(String riderId, RiderStatus status) {
        Rider rider = riders.get(riderId);
        if (rider != null) {
            rider.setStatus(status);
        }
    }
}
