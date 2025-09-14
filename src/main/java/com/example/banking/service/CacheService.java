package com.example.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void cacheUserSession(String username, String token) {
        redisTemplate.opsForValue().set("session:" + username, token, 24, TimeUnit.HOURS);
    }

    public String getUserSession(String username) {
        return (String) redisTemplate.opsForValue().get("session:" + username);
    }

    public void invalidateUserSession(String username) {
        redisTemplate.delete("session:" + username);
    }

    public void cacheAccountBalance(String accountNumber, Object balance) {
        redisTemplate.opsForValue().set("balance:" + accountNumber, balance, 10, TimeUnit.MINUTES);
    }

    public Object getCachedAccountBalance(String accountNumber) {
        return redisTemplate.opsForValue().get("balance:" + accountNumber);
    }
}