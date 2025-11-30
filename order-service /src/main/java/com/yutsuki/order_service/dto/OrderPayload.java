package com.yutsuki.order_service.dto;

public record OrderPayload(String id,
                           String productName,
                           Integer amount,
                           String status
) {
}
