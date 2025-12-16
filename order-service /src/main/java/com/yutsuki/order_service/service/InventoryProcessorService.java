package com.yutsuki.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yutsuki.order_service.dto.InventoryPayload;
import com.yutsuki.order_service.entity.Order;
import com.yutsuki.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class InventoryProcessorService {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "INVENTORY_RESERVED", groupId = "order-group")
    @Transactional
    public void processInventoryReservedEvent(@Payload String message
    ) {
        try {
            var payload = objectMapper.readValue(message, InventoryPayload.class);
            getAndUpdateOrder(UUID.fromString(payload.getOrderId()), Order.OrderStatus.SUCCESS);
        } catch (JsonProcessingException e) {
            log.error("ProcessInventoryReservedEvent-[error]. ", e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "INVENTORY_REJECTED", groupId = "order-group")
    @Transactional
    public void processInventoryRejectedEvent(@Payload String message
    ) {
        try {
            var payload = objectMapper.readValue(message, InventoryPayload.class);
            getAndUpdateOrder(UUID.fromString(payload.getOrderId()), Order.OrderStatus.CANCEL);
        } catch (JsonProcessingException e) {
            log.error("ProcessInventoryRejectedEvent-[error]. ", e);
            throw new RuntimeException(e);
        }
    }

    private void getAndUpdateOrder(UUID orderId, Order.OrderStatus status) {
        var optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            var order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
        }
    }
}
