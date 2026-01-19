package com.demo.cqrsproject.cqrs.command.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    private String orderId;
    private String reason;
}
