package com.demo.cqrsproject.cqrs.query.projection;

import com.demo.cqrsproject.cqrs.command.events.OrderCancelledEvent;
import com.demo.cqrsproject.cqrs.command.events.OrderConfirmedEvent;
import com.demo.cqrsproject.cqrs.command.events.OrderCreatedEvent;
import com.demo.cqrsproject.cqrs.query.OrderView;
import com.demo.cqrsproject.cqrs.query.OrderViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProjection {

    private final OrderViewRepository repository;

    @EventHandler
    @Transactional
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent: {}", event.getOrderId());

        OrderView view = new OrderView();
        view.setOrderId(event.getOrderId());
        view.setProductName(event.getProductName());
        view.setAmount(event.getAmount());
        view.setCustomerId(event.getCustomerId());
        view.setStatus(event.getStatus().toString());
        view.setCreatedAt(new Date());


        repository.save(view);
        log.info("OrderView saved: {}", event.getOrderId());
    }

    @EventHandler
    @Transactional
    public void on(OrderConfirmedEvent event) {
        OrderView view = repository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("OrderView not found: " + event.getOrderId()));

        view.setStatus("CONFIRMED");
        repository.save(view);
        log.info("OrderView updated to CONFIRMED: {}", event.getOrderId());
    }

    @EventHandler
    @Transactional
    public void on(OrderCancelledEvent event) {
        OrderView view = repository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("OrderView not found: " + event.getOrderId()));

        view.setStatus("CANCELLED");
        repository.save(view);
        log.info("OrderView updated to CANCELLED: {}", event.getOrderId());
    }
}
