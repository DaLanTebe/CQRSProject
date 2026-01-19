package com.demo.cqrsproject.cqrs.api;


import com.demo.cqrsproject.cqrs.api.dto.CancelRequest;
import com.demo.cqrsproject.cqrs.api.dto.CreateOrderRequest;
import com.demo.cqrsproject.cqrs.command.OrderStatus;
import com.demo.cqrsproject.cqrs.command.commands.CancelOrderCommand;
import com.demo.cqrsproject.cqrs.command.commands.ConfirmOrderCommand;
import com.demo.cqrsproject.cqrs.command.commands.CreateOrderCommand;
import com.demo.cqrsproject.cqrs.query.OrderView;
import com.demo.cqrsproject.cqrs.query.queries.GetOrderByIdQuery;
import com.demo.cqrsproject.cqrs.query.queries.GetOrdersByCustomerQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.IdentifierFactory;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final IdentifierFactory identifierFactory = IdentifierFactory.getInstance();

    @PostMapping("/v1")
    public ResponseEntity<CompletableFuture<String>> createOrderV1(
            @RequestBody CreateOrderRequest request) {

        String orderId = identifierFactory.generateIdentifier();
        log.info("Creating order with id: {}", orderId);

        CreateOrderCommand command = new CreateOrderCommand();
        command.setOrderId(orderId);
        command.setProductName(request.getProductName());
        command.setAmount(request.getAmount());
        command.setCustomerId(request.getCustomerId());

        CompletableFuture<String> result = commandGateway.send(command)
                .thenApply(v -> orderId)
                .exceptionally(ex -> {
                    log.error("Error creating order", ex);
                    throw new RuntimeException("Failed to create order", ex);
                });

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(result);
    }

    @PutMapping("/v1/{id}/confirm")
    public CompletableFuture<Void> confirmOrderV1(@PathVariable String id) {
        ConfirmOrderCommand command = new ConfirmOrderCommand();
        command.setOrderId(id);
        return commandGateway.send(command);
    }

    @PutMapping("/v1/{id}/cancel")
    public CompletableFuture<Void> cancelOrderV1(
            @PathVariable String id,
            @RequestBody CancelRequest request) {

        CancelOrderCommand command = new CancelOrderCommand();
        command.setOrderId(id);
        command.setReason(request.getReason());
        return commandGateway.send(command);
    }

    @GetMapping("/v1/{id}")
    public CompletableFuture<OrderView> getOrderV1(@PathVariable String id) {
        GetOrderByIdQuery query = new GetOrderByIdQuery();
        query.setOrderId(id);

        return queryGateway.query(
                query,
                ResponseTypes.instanceOf(OrderView.class)
        );
    }

    @GetMapping("/v1")
    public CompletableFuture<List<OrderView>> getOrdersByCustomerV1(
            @RequestParam String customerId) {

        GetOrdersByCustomerQuery query = new GetOrdersByCustomerQuery();
        query.setCustomerId(customerId);

        return queryGateway.query(
                query,
                ResponseTypes.multipleInstancesOf(OrderView.class)
        );
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = identifierFactory.generateIdentifier();
        log.info("🚀 Creating order: {}", orderId);

        CreateOrderCommand command = new CreateOrderCommand();
        command.setOrderId(orderId);
        command.setProductName(request.getProductName());
        command.setAmount(request.getAmount());
        command.setCustomerId(request.getCustomerId());

        commandGateway.send(command);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .header("X-Order-Id", orderId)
                .header("Location", "/api/orders/" + orderId)
                .body(new OrderResponse(orderId, OrderStatus.CONFIRMED, "Order accepted for processing"));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable String id) {
        log.info("✅ Confirming order: {}", id);

        ConfirmOrderCommand command = new ConfirmOrderCommand();
        command.setOrderId(id);

        commandGateway.send(command);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Confirmation request accepted");
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable String id,
            @RequestBody CancelRequest request) {

        log.info("❌ Cancelling order: {}, reason: {}", id, request.getReason());

        CancelOrderCommand command = new CancelOrderCommand();
        command.setOrderId(id);
        command.setReason(request.getReason());

        commandGateway.send(command);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Cancellation request accepted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        log.info("📖 Getting order: {}", id);

        GetOrderByIdQuery query = new GetOrderByIdQuery();
        query.setOrderId(id);

        try {
            OrderView order = queryGateway.query(
                    query,
                    ResponseTypes.instanceOf(OrderView.class)
            ).get(2, TimeUnit.SECONDS);

            return ResponseEntity.ok(order);

        } catch (java.util.concurrent.TimeoutException e) {
            log.warn("Timeout getting order: {}", id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Order not found or not yet processed");
        } catch (Exception e) {
            log.error("Error getting order: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrdersByCustomer(
            @RequestParam String customerId) {

        log.info("📚 Getting orders for customer: {}", customerId);

        GetOrdersByCustomerQuery query = new GetOrdersByCustomerQuery();
        query.setCustomerId(customerId);

        try {
            List<OrderView> orders = queryGateway.query(
                    query,
                    ResponseTypes.multipleInstancesOf(OrderView.class)
            ).get(2, TimeUnit.SECONDS);

            return ResponseEntity.ok(orders);

        } catch (TimeoutException e) {
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            log.error("Error getting orders", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

    @PostMapping("/simple")
    public ResponseEntity<String> createSimpleOrder() {
        String orderId = identifierFactory.generateIdentifier();

        CreateOrderCommand command = new CreateOrderCommand();
        command.setOrderId(orderId);
        command.setProductName("Test Product");
        command.setAmount(new BigDecimal("99.99"));
        command.setCustomerId("test-" + System.currentTimeMillis());

        commandGateway.send(command);

        return ResponseEntity.ok("Test order accepted. ID: " + orderId);
    }

    @GetMapping("/{id}/raw")
    public CompletableFuture<OrderView> getOrderRaw(@PathVariable String id) {
        GetOrderByIdQuery query = new GetOrderByIdQuery();
        query.setOrderId(id);
        return queryGateway.query(query, ResponseTypes.instanceOf(OrderView.class));
    }

    @GetMapping("/raw")
    public CompletableFuture<List<OrderView>> getOrdersRaw(@RequestParam String customerId) {
        GetOrdersByCustomerQuery query = new GetOrdersByCustomerQuery();
        query.setCustomerId(customerId);
        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(OrderView.class));
    }
}