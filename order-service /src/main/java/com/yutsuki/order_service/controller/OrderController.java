package com.yutsuki.order_service.controller;

import com.yutsuki.order_service.dto.CreateOrderDto;
import com.yutsuki.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("hi");
    }

    @PostMapping
    public ResponseEntity<String>createOrder(@RequestBody CreateOrderDto request){
        return orderService.createOrder(request);

    }
}
