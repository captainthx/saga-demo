package com.yutsuki.order_service.controller;

import com.yutsuki.order_service.dto.CreateOrderDto;
import com.yutsuki.order_service.dto.OrderResponse;
import com.yutsuki.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderDto request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return orderService.getAllOrders();
    }
}
