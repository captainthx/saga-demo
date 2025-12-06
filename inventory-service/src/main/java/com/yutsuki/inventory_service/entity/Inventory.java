package com.yutsuki.inventory_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Integer quantity;


    public enum InventoryStatus{
        INVENTORY_RESERVED,
        INVENTORY_REJECTED
    }
}
