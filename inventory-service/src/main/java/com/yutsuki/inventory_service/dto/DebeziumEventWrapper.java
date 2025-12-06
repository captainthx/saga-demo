package com.yutsuki.inventory_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebeziumEventWrapper {
    private String payload;
}
