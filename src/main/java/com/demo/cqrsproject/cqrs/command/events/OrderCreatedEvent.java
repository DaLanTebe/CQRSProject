package com.demo.cqrsproject.cqrs.command.events;

import com.demo.cqrsproject.cqrs.command.OrderStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    @NonNull
    private String orderId;
    @NonNull
    private String productName;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private String customerId;
    private OrderStatus status = OrderStatus.CREATED;
}
