package com.example.banking.mapper;

import com.example.banking.dto.AccountDto;
import com.example.banking.dto.UserDto;
import com.example.banking.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    
    public AccountDto toDto(Account account) {
        if (account == null) return null;
        
        UserDto userDto = new UserDto(
            account.getUser().getId(),
            account.getUser().getUsername(),
            account.getUser().getEmail(),
            account.getUser().getFullName(),
            account.getUser().getCreatedAt()
        );
        
        return new AccountDto(
            account.getId(),
            account.getAccountNumber(),
            account.getBalance(),
            account.getAccountType().toString(),
            account.getCreatedAt(),
            userDto
        );
    }
}