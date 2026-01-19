package com.demo.cqrsproject.cqrs.command;

import com.demo.cqrsproject.cqrs.command.commands.*;
import com.demo.cqrsproject.cqrs.command.events.OrderCancelledEvent;
import com.demo.cqrsproject.cqrs.command.events.OrderConfirmedEvent;
import com.demo.cqrsproject.cqrs.command.events.OrderCreatedEvent;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import java.math.BigDecimal;

@Aggregate
@Entity
@Table(name = "order_aggregate")
@Data
@NoArgsConstructor
public class OrderAggregate {

    @Id
    @AggregateIdentifier
    private String orderId;

    private String productName;
    private BigDecimal amount;
    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        if (command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(),
                command.getProductName(),
                command.getAmount(),
                command.getCustomerId()));
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be confirmed");
        }
        AggregateLifecycle.apply(new OrderConfirmedEvent(command.getOrderId()));
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        AggregateLifecycle.apply(new OrderCancelledEvent(
                command.getOrderId(),
                command.getReason()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.productName = event.getProductName();
        this.amount = event.getAmount();
        this.customerId = event.getCustomerId();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.status = OrderStatus.CONFIRMED;
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
    }
}
