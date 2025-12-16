package com.yutsuki.order_service.service;

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
        if (outboxEventIdHeader == null) {
            log.error("Outbox Event ID is missing in header. Cannot process message.");
            return;
        }
        try {
            UUID outboxEventId = UUID.fromString(outboxEventIdHeader);
            outboxEventRepository.markAsSent(outboxEventId);
            log.info("Successfully updated outbox event {} to status SENT", outboxEventId);
        } catch (Exception e) {
            log.error("Error processing outbox event with id {}: {}", outboxEventIdHeader, e.getMessage(), e);
        }
    }
}
