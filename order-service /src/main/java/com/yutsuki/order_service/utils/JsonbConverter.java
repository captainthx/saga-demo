package com.yutsuki.order_service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

// @Component ไม่จำเป็นต้องใส่ก็ได้ เพราะ JPA จะจัดการเอง
@Converter(autoApply = false) // บอกว่าจะใช้แบบ manual ผ่าน @Convert
public class JsonbConverter implements AttributeConverter<String, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // ตอนเซฟลง DB (String -> jsonb String)
        // ใน PostgreSQL, การส่ง JSON string ไปตรงๆ ก็ถือว่าถูกต้องแล้วสำหรับ jsonb
        // แต่เพื่อความปลอดภัย เราจะตรวจสอบว่ามันเป็น JSON ที่ถูกต้องหรือไม่
        if (attribute == null) {
            return null;
        }
        try {
            objectMapper.readTree(attribute);
            return attribute;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON string: " + attribute, e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // ตอนโหลดจาก DB (jsonb String -> String)
        // ในกรณีนี้เราไม่ต้องทำอะไรเพิ่มเติม
        return dbData;
    }
}