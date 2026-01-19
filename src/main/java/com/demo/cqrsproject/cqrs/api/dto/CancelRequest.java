package com.demo.cqrsproject.cqrs.api.dto;

import lombok.Data;

@Data
public class CancelRequest {
    private String reason;
}
