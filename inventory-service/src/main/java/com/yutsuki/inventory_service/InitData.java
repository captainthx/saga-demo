package com.yutsuki.inventory_service;

import com.yutsuki.inventory_service.entity.Inventory;
import com.yutsuki.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;
    private final List<String> productNames = List.of("coffee", "car", "phone", "banana");

    @Override
    public void run(String... args) throws Exception {
        log.info("start init data .....");
        var count = inventoryRepository.count();
        if (count > 0) {
            log.info("product is create already!!");
            return;
        }
        for (var name : productNames) {
            var inventory = createInventory(name);
            inventoryRepository.save(inventory);
            log.info("create inventory product :{}", name);
        }
    }

    private Inventory createInventory(String productName) {
        var inventory = new Inventory();
        inventory.setProductName(productName);
        inventory.setQuantity(10);
        return inventory;
    }
}
