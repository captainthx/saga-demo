package com.yutsuki.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yutsuki.order_service.dto.CreateOrderDto;
import com.yutsuki.order_service.dto.OrderPayload;
import com.yutsuki.order_service.dto.OrderResponse;
import com.yutsuki.order_service.entity.Order;
import com.yutsuki.order_service.entity.OutBoxEvent;
import com.yutsuki.order_service.repository.OrderRepository;
import com.yutsuki.order_service.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public ResponseEntity<String> createOrder(CreateOrderDto request) {
        var entity = new Order();
        entity.setProductName(request.productName());
        entity.setAmount(request.amount());
        entity.setStatus(Order.OrderStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());
        var order = orderRepository.save(entity);

        var outBoxEvent = new OutBoxEvent();
        outBoxEvent.setAggregateid(order.getId());
        outBoxEvent.setPayload(convertOrderToJson(order));
        outBoxEvent.setAggregatetype("ORDER_CREATED");
        outBoxEvent.setStatus(OutBoxEvent.OutboxEvent.NEW);
        outBoxEvent.setTimestamp(LocalDateTime.now());
        outboxEventRepository.save(outBoxEvent);
        return ResponseEntity.ok().body("create order success.");
    }

    public ResponseEntity<OrderResponse> getOrderById(String orderId) {
        var optionalOrder = orderRepository.findById(UUID.fromString(orderId));
        if (optionalOrder.isEmpty()) {
            log.warn("GetOrderById-[error].(not found order) orderId:{}", orderId);
            return ResponseEntity.notFound().build();
        }

        var order = optionalOrder.get();
        var response = OrderResponse.fromEntity(order);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        var orders = orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok().body(orders);
    }

    private String convertOrderToJson(Order order) {
        try {
            var orderPayload = new OrderPayload(
                    order.getId().toString(),
                    order.getProductName(),
                    order.getAmount(),
                    order.getStatus().name()
            );
            return new ObjectMapper().writeValueAsString(orderPayload);
        } catch (JsonProcessingException e) {
            log.error("ConvertOrderToJson-[error]. error: ", e);
            throw new RuntimeException(e);
        }
    }
}

