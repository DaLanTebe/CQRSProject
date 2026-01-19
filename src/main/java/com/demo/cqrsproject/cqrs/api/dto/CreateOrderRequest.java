package com.demo.cqrsproject.cqrs.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private String productName;
    private BigDecimal amount;
    private String customerId;
}
