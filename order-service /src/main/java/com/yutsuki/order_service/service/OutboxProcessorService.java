package com.yutsuki.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yutsuki.order_service.entity.OutBoxEvent;
import com.yutsuki.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutboxProcessorService {
    private final OutboxEventRepository outboxEventRepository;

    @KafkaListener(topics = "outbox.event.ORDER_CREATED", groupId = "order-group")
    @Transactional
    public void processOrderCreatedEvent(@Payload String message,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String messageKey,
                                         @Header(name = "id", required = false) String outboxEventIdHeader
    ) {
        log.info("Received message from topic 'outbox.event.ORDER_CREATED'");
        log.info("Payload: {}", message);
        log.info("Message Key (Aggregate ID): {}", messageKey);
        log.info("Outbox Event ID from Header: {}", outboxEventIdHeader);
        if (outboxEventIdHeader == null) {
            log.error("Outbox Event ID is missing in header. Cannot process message.");
            return;
        }
        try {
            // แปลง String ของ outboxEventIdHeader เป็น UUID
            UUID outboxEventId = UUID.fromString(outboxEventIdHeader);
            // อัปเดตสถานะเป็น SENT
            outboxEventRepository.markAsSent(outboxEventId);
            log.info("Successfully updated outbox event {} to status SENT", outboxEventId);
        } catch (Exception e) {
            log.error("Error processing outbox event with id {}: {}", outboxEventIdHeader, e.getMessage(), e);
        }
    }
}
