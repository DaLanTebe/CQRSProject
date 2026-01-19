package com.demo.cqrsproject.cqrs.command.commands;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String productName;
    private BigDecimal amount;
    private String customerId;
}
