package com.example.banking.controller;

import com.example.banking.dto.TransactionHistoryDto;
import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.mapper.TransactionMapper;
import com.example.banking.service.AccountService;
import com.example.banking.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionMapper transactionMapper;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request) {
        log.info("Depositing to account: {}", request.get("accountNumber"));
        String accountNumber = (String) request.get("accountNumber");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");
        
        Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        Transaction transaction = transactionService.deposit(account, amount, description);
        return ResponseEntity.ok(transactionMapper.toResponseDto(transaction, "Deposit done successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request) {
        log.info("Withdrawing from account: {}", request.get("accountNumber"));
        String accountNumber = (String) request.get("accountNumber");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.get("description");
        
        Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        Transaction transaction = transactionService.withdraw(account, amount, description);
        return ResponseEntity.ok(transactionMapper.toResponseDto(transaction, "Withdrwal done successfully"));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable String accountNumber) {
        log.info("Fetching transaction history for account: {}", accountNumber);
        Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        List<Transaction> transactions = transactionService.getTransactionHistory(account.getId());
        
        List<TransactionHistoryDto> transactionHistoryDtos = transactions.stream()
                .map(transactionMapper::toHistoryDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(transactionHistoryDtos);
    }
}