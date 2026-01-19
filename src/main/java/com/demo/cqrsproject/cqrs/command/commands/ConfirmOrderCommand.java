package com.demo.cqrsproject.cqrs.command.commands;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@NoArgsConstructor
public class ConfirmOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
}