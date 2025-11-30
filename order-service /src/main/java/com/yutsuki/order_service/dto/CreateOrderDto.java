package com.yutsuki.order_service.dto;

public record CreateOrderDto(String productName,
                             Integer amount) {
}
