package com.example.banking.service;

import com.example.banking.entity.Account;
import com.example.banking.entity.Transaction;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;

    @Transactional
    public Transaction deposit(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        BigDecimal newBalance = account.getBalance().add(amount);
        accountService.updateBalance(account, newBalance);
        
        Transaction transaction = new Transaction(amount, Transaction.TransactionType.DEPOSIT, description, account);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(Account account, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + account.getBalance());
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountService.updateBalance(account, newBalance);
        
        Transaction transaction = new Transaction(amount, Transaction.TransactionType.WITHDRAWAL, description, account);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}