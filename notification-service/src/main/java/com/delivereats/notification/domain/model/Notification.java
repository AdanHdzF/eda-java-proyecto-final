package com.delivereats.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Notification {

    private UUID id;
    private String orderId;
    private NotificationType type;
    private String recipient;
    private String message;
    private Instant sentAt;

    public Notification(UUID id, String orderId, NotificationType type, String recipient, String message, Instant sentAt) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.recipient = recipient;
        this.message = message;
        this.sentAt = sentAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
