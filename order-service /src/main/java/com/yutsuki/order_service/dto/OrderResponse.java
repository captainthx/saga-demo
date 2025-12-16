package com.yutsuki.order_service.dto;

import com.yutsuki.order_service.entity.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aspectj.weaver.ast.Or;

import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderResponse {
    private String id;
    private String productName;
    private Integer amount;
    private Order.OrderStatus status;


    public static OrderResponse fromEntity(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(String.valueOf(order.getId()));
        res.setProductName(order.getProductName());
        res.setStatus(order.getStatus());
        res.setAmount(order.getAmount());
        return res;
    }
}
