package com.delivereats.kitchen.application.service;

import java.util.List;

import com.delivereats.kitchen.application.dto.KitchenConfirmationRequest;
import com.delivereats.kitchen.application.dto.KitchenConfirmationResponse;
import com.delivereats.kitchen.application.dto.PaymentRequest;
import com.delivereats.kitchen.domain.event.KitchenConfirmedEvent;
import com.delivereats.kitchen.domain.model.KitchenOrder;
import com.delivereats.kitchen.domain.model.KitchenStatus;
import com.delivereats.kitchen.domain.port.in.ConfirmOrderUseCase;
import com.delivereats.kitchen.domain.port.out.KitchenOrderRepositoryPort;
import com.delivereats.kitchen.domain.port.out.PaymentPort;
import com.delivereats.kitchen.infrastructure.adapter.out.event.KitchenPublisher;

public class KitchenApplicationService implements ConfirmOrderUseCase {

	private final KitchenOrderRepositoryPort kitchenOrderRepository;
	private final PaymentPort paymentPort;
	private final KitchenPublisher kitchenPublisher;

	public KitchenApplicationService(KitchenOrderRepositoryPort kitchenOrderRepository, PaymentPort paymentPort,
			KitchenPublisher kitchenPublisher) {
		this.kitchenOrderRepository = kitchenOrderRepository;
		this.paymentPort = paymentPort;
		this.kitchenPublisher = kitchenPublisher;
	}

	@Override
	public KitchenConfirmationResponse confirmOrder(KitchenConfirmationRequest request) {
		System.out.println("[Kitchen] Checking ingredients for order: " + request.orderId());

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while checking ingredients", e);
		}

		boolean hasImpossiblePizza = request.items().stream()
				.anyMatch(item -> "Pizza Imposible".equals(item.productName()));

		if (hasImpossiblePizza) {
			System.out.println("[Kitchen] Order " + request.orderId() + " FAILED: Pizza Imposible not available");
			List<String> itemNames = request.items().stream()
					.map(item -> item.productName())
					.toList();
			KitchenOrder order = new KitchenOrder(request.orderId(), itemNames, KitchenStatus.FAILED, 0);
			kitchenOrderRepository.save(order);
			return new KitchenConfirmationResponse(false, 0);
		}

		List<String> itemNames = request.items().stream()
				.map(item -> item.productName())
				.toList();
		KitchenOrder order = new KitchenOrder(request.orderId(), itemNames, KitchenStatus.CONFIRMED, 25);
		kitchenOrderRepository.save(order);

		int estimatedMinutes = 25;

		kitchenPublisher.publish("kitchen.confirmed",
				new KitchenConfirmedEvent(request.orderId(), estimatedMinutes));

		double totalAmount = request.items().stream()
				.mapToDouble(item -> item.price() * item.quantity())
				.sum();

		System.out.println("[Kitchen] Requesting payment of $" + totalAmount + " for order: " + request.orderId());
		paymentPort.requestPayment(new PaymentRequest(request.orderId(), totalAmount, "customer"));

		System.out.println(
				"[Kitchen] Order " + request.orderId() + " CONFIRMED. Estimated: " + estimatedMinutes + " min");
		return new KitchenConfirmationResponse(true, estimatedMinutes);
	}
}
