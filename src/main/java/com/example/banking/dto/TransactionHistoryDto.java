package com.example.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {
    private Long transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}