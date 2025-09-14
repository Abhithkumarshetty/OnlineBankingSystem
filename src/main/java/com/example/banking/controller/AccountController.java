package com.example.banking.controller;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;
import com.example.banking.entity.User;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.mapper.AccountMapper;
import com.example.banking.service.AccountService;
import com.example.banking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountMapper accountMapper;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, Object> request) {
        log.info("Creating account: {}", request.get("username"));
        String username = (String) request.get("username");
        String accountTypeStr = (String) request.get("accountType");
        BigDecimal initialBalance = new BigDecimal(request.get("initialBalance").toString());
        
        User user = userService.getUserByUsername(username);
        Account.AccountType accountType = Account.AccountType.valueOf(accountTypeStr.toUpperCase());
        Account account = accountService.createAccount(user, accountType, initialBalance);
        
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserAccounts(@PathVariable String username) {
        log.info("Fetching accounts for user: {}", username);
        User user = userService.getUserByUsername(username);
        List<Account> accounts = accountService.getAccountsByUserId(user.getId());
        List<AccountDto> accountDtos = accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDtos);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) {
        log.info("Fetching account: {}", accountNumber);
        Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {
        log.info("Fetching balance for account: {}", accountNumber);
        BigDecimal cachedBalance = accountService.getCachedBalance(accountNumber);
        if (cachedBalance != null) {
            return ResponseEntity.ok(Map.of("balance", cachedBalance, "source", "cache"));
        }
        
        Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        
        return ResponseEntity.ok(Map.of("balance", account.getBalance(), "source", "database"));
    }
}