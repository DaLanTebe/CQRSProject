package com.demo.cqrsproject.cqrs.query.handlers;

import com.demo.cqrsproject.cqrs.query.OrderView;
import com.demo.cqrsproject.cqrs.query.OrderViewRepository;
import com.demo.cqrsproject.cqrs.query.queries.GetOrderByIdQuery;
import com.demo.cqrsproject.cqrs.query.queries.GetOrdersByCustomerQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryHandler {

    private final OrderViewRepository repository;

    @QueryHandler
    public OrderView handle(GetOrderByIdQuery query) {
        return repository.findById(query.getOrderId())
                .orElseThrow(() -> new RuntimeException(
                        String.format("Order with id %s not found", query.getOrderId())
                ));
    }

    @QueryHandler
    public List<OrderView> handle(GetOrdersByCustomerQuery query) {
        List<OrderView> results = repository.findByCustomerId(query.getCustomerId());


        if (results == null) {
            return Collections.emptyList();
        }

        return List.copyOf(results);
    }
}

