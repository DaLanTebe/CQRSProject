package com.demo.cqrsproject.cqrs.api;

import com.demo.cqrsproject.cqrs.command.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String orderId;
    private OrderStatus status;
    private String message;
    private Instant timestamp = Instant.now();

    public OrderResponse(String orderId, OrderStatus status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }
}
