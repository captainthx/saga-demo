package com.yutsuki.inventory_service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderPayload {
    private String id;
    private int amount;
    private String status;
    private String productName;
}
