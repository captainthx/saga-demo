package com.yutsuki.inventory_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yutsuki.inventory_service.dto.DebeziumEventWrapper;
import com.yutsuki.inventory_service.dto.InventoryPayload;
import com.yutsuki.inventory_service.dto.OrderPayload;
import com.yutsuki.inventory_service.entity.Inventory;
import com.yutsuki.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "outbox.event.ORDER_CREATED", groupId = "inventory-group")
    public void inventoryProcess(@Payload String message
    ) {
        try {
            var wrapper = objectMapper.readValue(message, DebeziumEventWrapper.class);
            var payload = wrapper.getPayload();
            var orderPayload = objectMapper.readValue(payload, OrderPayload.class);

            var optionalInventory = inventoryRepository.findByProductName(orderPayload.getProductName());

            if (optionalInventory.isEmpty()) {
                log.warn("InventoryProcess-[error]. not found product!! payload: {}", orderPayload.toString());
                sendKafkaEvent(Inventory.InventoryStatus.INVENTORY_REJECTED.name(),
                        orderPayload.getId(),
                        Inventory.InventoryStatus.INVENTORY_REJECTED.name()
                );
                return;
            }
            var inventory = optionalInventory.get();
            if (inventory.getQuantity() <= 0){
                log.warn("InventoryProcess-[error]. out of stock!! payload: {}", orderPayload.toString());
                sendKafkaEvent(Inventory.InventoryStatus.INVENTORY_REJECTED.name(),
                        orderPayload.getId(),
                        Inventory.InventoryStatus.INVENTORY_REJECTED.name()
                );
                return;
            }
            var reservedStock = inventory.getQuantity() - orderPayload.getAmount();
            inventory.setQuantity(reservedStock);
            inventoryRepository.save(inventory);
            sendKafkaEvent(Inventory.InventoryStatus.INVENTORY_RESERVED.name(),
                    orderPayload.getId(),
                    Inventory.InventoryStatus.INVENTORY_RESERVED.name()
            );
            log.info("InventoryProcess-[success]. reserved inventory success! {}", orderPayload.toString());
        } catch (JsonProcessingException e) {
            log.error("InventoryProcess-[error]. processing Kafka message: {}", e.getMessage(), e);
        }
    }

    private void sendKafkaEvent(String topic, String orderId, String status) {
        var payload = new InventoryPayload();
        payload.setOrderId(orderId);
        payload.setStatus(status);
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            log.error("SendKafkaEvent-[error]. error: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
