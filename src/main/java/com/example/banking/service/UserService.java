package com.example.banking.service;

import com.example.banking.dto.UserDto;
import com.example.banking.entity.User;
import com.example.banking.exception.UserNotFoundException;
import com.example.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CacheService cacheService;

    public User createUser(String username, String password, String email, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, passwordEncoder.encode(password), email, fullName);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), 
                          user.getFullName(), user.getCreatedAt());
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void cacheUserSession(String username, String token) {
        cacheService.cacheUserSession(username, token);
    }

    public boolean isValidSession(String username, String token) {
        String cachedToken = cacheService.getUserSession(username);
        return token != null && token.equals(cachedToken);
    }

    public void logout(String username) {
        cacheService.invalidateUserSession(username);
    }
}