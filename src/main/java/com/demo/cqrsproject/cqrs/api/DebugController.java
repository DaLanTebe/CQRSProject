package com.demo.cqrsproject.cqrs.api;

import com.demo.cqrsproject.cqrs.command.commands.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final CommandGateway commandGateway;

    @GetMapping("/debug/axon")
    public Map<String, Object> debugAxon() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", new Date());
        result.put("application", "cqrs-demo");
        result.put("axonStatus", "CONNECTED");

        // Проверка порта Axon Server
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 8124), 3000);
            result.put("axonPort8124", "OPEN");
        } catch (Exception e) {
            result.put("axonPort8124", "CLOSED: " + e.getMessage());
        }

        // Проверка порта Axon UI
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 8024), 3000);
            result.put("axonUI8024", "OPEN");
        } catch (Exception e) {
            result.put("axonUI8024", "CLOSED: " + e.getMessage());
        }

        // Быстрая проверка команды
        try {
            String testId = "test-" + UUID.randomUUID().toString().substring(0, 8);
            CreateOrderCommand cmd = new CreateOrderCommand();
            cmd.setOrderId(testId);
            cmd.setProductName("Debug Test Product");
            cmd.setAmount(new BigDecimal("99.99"));
            cmd.setCustomerId("debug-user");

            commandGateway.send(cmd);
            result.put("commandTest", "SENT - ID: " + testId);
        } catch (Exception e) {
            result.put("commandTest", "ERROR: " + e.getMessage());
        }

        log.info("Debug endpoint called: {}", result);
        return result;
    }

    @GetMapping("/debug/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "CQRS Demo");
        health.put("timestamp", new Date().toString());
        return health;
    }
}
