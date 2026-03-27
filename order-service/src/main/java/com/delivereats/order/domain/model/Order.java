package com.delivereats.order.domain.model;

import java.util.List;
import java.util.UUID;

public class Order {

    private final String id;
    private final String customerId;
    private final String customerName;
    private final String restaurantName;
    private final List<OrderItem> items;
    private final double totalAmount;
    private OrderStatus status;

    public Order(String customerId, String customerName, String restaurantName, List<OrderItem> items) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.customerName = customerName;
        this.restaurantName = restaurantName;
        this.items = List.copyOf(items);
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.price() * item.quantity())
                .sum();
        this.status = OrderStatus.CREATED;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getRestaurantName() { return restaurantName; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
