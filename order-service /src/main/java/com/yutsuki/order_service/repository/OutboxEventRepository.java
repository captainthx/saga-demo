package com.yutsuki.order_service.repository;

import com.yutsuki.order_service.entity.OutBoxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutBoxEvent, UUID> {

    @Modifying
    @Query("UPDATE OutBoxEvent o SET o.status = 'SENT' WHERE o.id = :id")
    void markAsSent(@Param("id") UUID id);
}
