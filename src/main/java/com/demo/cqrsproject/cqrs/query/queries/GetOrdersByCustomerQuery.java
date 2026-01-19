package com.demo.cqrsproject.cqrs.query.queries;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetOrdersByCustomerQuery {
    private String customerId;
}
