package com.banking.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long fromAccountId;
    private String toAccountNumber;
    private Double amount;
    private String description;
}
