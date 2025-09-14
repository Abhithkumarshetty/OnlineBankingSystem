package com.example.banking.mapper;

import com.example.banking.dto.TransactionResponseDto;
import com.example.banking.dto.TransactionHistoryDto;
import com.example.banking.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    
    public TransactionResponseDto toResponseDto(Transaction transaction, String message) {
        return new TransactionResponseDto(
            transaction.getId(),
            transaction.getAccount().getUser().getUsername(),
            message
        );
    }
    
    public TransactionHistoryDto toHistoryDto(Transaction transaction) {
        return new TransactionHistoryDto(
            transaction.getId(),
            transaction.getTransactionType().toString(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }
}