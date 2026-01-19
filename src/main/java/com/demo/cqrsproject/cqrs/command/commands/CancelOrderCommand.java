package com.demo.cqrsproject.cqrs.command.commands;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class CancelOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String reason;
}
