package com.demo.cqrsproject.cqrs.query;

import lombok.Data;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "order_view")
@Data
public class OrderView {
    @Id
    private String orderId;

    private String productName;
    private BigDecimal amount;
    private String customerId;
    private String status;

    private String customerName;
    private String productCategory;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
