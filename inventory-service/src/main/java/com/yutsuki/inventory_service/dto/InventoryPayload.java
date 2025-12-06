package com.yutsuki.inventory_service.dto;

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
