package com.example.banking.service;

import com.example.banking.entity.Account;
import com.example.banking.entity.User;
import com.example.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CacheService cacheService;

    public Account createAccount(User user, Account.AccountType accountType, BigDecimal initialBalance) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber, initialBalance, accountType, user);
        Account savedAccount = accountRepository.save(account);
        cacheService.cacheAccountBalance(accountNumber, initialBalance);
        return savedAccount;
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account updateBalance(Account account, BigDecimal newBalance) {
        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);
        cacheService.cacheAccountBalance(account.getAccountNumber(), newBalance);
        return updatedAccount;
    }

    public BigDecimal getCachedBalance(String accountNumber) {
        Object cachedBalance = cacheService.getCachedAccountBalance(accountNumber);
        return cachedBalance != null ? (BigDecimal) cachedBalance : null;
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%010d", new Random().nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}