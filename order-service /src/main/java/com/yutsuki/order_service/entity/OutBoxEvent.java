package com.yutsuki.order_service.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "outbox_event")
public class OutBoxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID aggregateid;

    private String aggregatetype;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxEvent status;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public enum OutboxEvent {
        NEW,
        SENT
    }
}
