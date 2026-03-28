package com.delivereats.order.application.service;

import java.util.List;

import com.delivereats.order.application.dto.CreateOrderRequest;
import com.delivereats.order.application.dto.OrderResponse;
import com.delivereats.order.application.dto.OrderStatusDto;
import com.delivereats.order.domain.event.OrderCreatedEvent;
import com.delivereats.order.domain.exception.OrderNotFoundException;
import com.delivereats.order.domain.model.Order;
import com.delivereats.order.domain.model.OrderItem;
import com.delivereats.order.domain.model.OrderStatus;
import com.delivereats.order.domain.port.in.CancelOrderUseCase;
import com.delivereats.order.domain.port.in.CreateOrderUseCase;
import com.delivereats.order.domain.port.in.GetOrderStatusUseCase;
import com.delivereats.order.domain.port.out.KitchenPort;
import com.delivereats.order.domain.port.out.OrderRepositoryPort;
import com.delivereats.order.infrastructure.adapter.out.event.OrderPublisher;

public class OrderApplicationService implements CreateOrderUseCase, GetOrderStatusUseCase, CancelOrderUseCase {

	private final OrderRepositoryPort orderRepository;
	private final KitchenPort kitchenPort;
	private final OrderPublisher orderPublisher;

	public OrderApplicationService(OrderRepositoryPort orderRepository, KitchenPort kitchenPort,
			OrderPublisher orderPublisher) {
		this.orderRepository = orderRepository;
		this.kitchenPort = kitchenPort;
		this.orderPublisher = orderPublisher;
	}

	@Override
	public OrderResponse createOrder(CreateOrderRequest request) {
		List<OrderItem> items = request.items().stream()
				.map(dto -> new OrderItem(dto.productName(), dto.price(), dto.quantity()))
				.toList();

		Order order = new Order("customer-1", request.customerName(), request.restaurantName(), items);
		orderRepository.save(order);

		System.out.println("[OrderService] Order " + order.getId() + " created. Calling Kitchen Service (SYNC)...");

		orderPublisher.publish("orders.created",
				new OrderCreatedEvent(order.getId(), order.getCustomerId(),
						order.getCustomerName(),
						order.getRestaurantName(), request.items(), order.getTotalAmount()));

		// ── SYNCHRONOUS blocking call to Kitchen Service ──
		// List<OrderItemDto> itemDtos = request.items();
		// KitchenConfirmationRequest kitchenRequest = new KitchenConfirmationRequest(
		// order.getId(), itemDtos, request.restaurantName());

		// KitchenConfirmationResponse kitchenResponse =
		// kitchenPort.requestConfirmation(kitchenRequest);

		// if (kitchenResponse.confirmed()) {
		// order.updateStatus(OrderStatus.KITCHEN_CONFIRMED);
		// orderRepository.updateStatus(order.getId(), OrderStatus.KITCHEN_CONFIRMED);
		// System.out.println("[OrderService] Kitchen confirmed order " + order.getId()
		// + " (ETA: " + kitchenResponse.estimatedMinutes() + " min)");
		// }

		// return new OrderResponse(
		// order.getId(),
		// order.getStatus().name(),
		// order.getTotalAmount(),
		// "Order processed. Kitchen " + (kitchenResponse.confirmed() ? "confirmed" :
		// "rejected")
		// + ". ETA: " + kitchenResponse.estimatedMinutes() + " min");
		return new OrderResponse(
				order.getId(),
				order.getStatus().name(),
				order.getTotalAmount(),
				"Order created and published to Kitchen Service. Awaiting confirmation...");
	}

	@Override
	public OrderStatusDto getOrderStatus(String orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		return new OrderStatusDto(
				order.getId(),
				order.getCustomerName(),
				order.getRestaurantName(),
				order.getStatus().name(),
				order.getTotalAmount(),
				null, null, null, null);
	}

	@Override
	public OrderStatusDto updateOrderStatus(String orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		order.updateStatus(newStatus);
		orderRepository.updateStatus(orderId, newStatus);
		System.out.println("[OrderService] Order " + orderId + " status updated to " + newStatus);

		return new OrderStatusDto(
				order.getId(),
				order.getCustomerName(),
				order.getRestaurantName(),
				order.getStatus().name(),
				order.getTotalAmount(),
				null, null, null, null);
	}

	@Override
	public void cancelOrder(String orderId, String reason) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(orderId));

		order.updateStatus(OrderStatus.CANCELLED);
		orderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
		System.out.println("[OrderService] Order " + orderId + " cancelled. Reason: " + reason);
	}
}
