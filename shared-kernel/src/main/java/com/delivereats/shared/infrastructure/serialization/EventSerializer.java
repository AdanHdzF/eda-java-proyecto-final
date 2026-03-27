package com.delivereats.shared.infrastructure.serialization;

public interface EventSerializer {
    String serialize(Object event);
    <T> T deserialize(String json, Class<T> type);
}
