package com.yutsuki.order_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryPayload {
    private String orderId;
    private String status;
}
