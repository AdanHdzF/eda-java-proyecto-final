package com.delivereats.shared.infrastructure.messaging;

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}
