package com.example.banking.controller;

import com.example.banking.dto.JwtResponse;
import com.example.banking.dto.LoginRequest;
import com.example.banking.dto.UserDto;
import com.example.banking.entity.User;
import com.example.banking.exception.InvalidCredentialsException;
import com.example.banking.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        log.info("Registering user: {}", request.get("username"));
        User user = userService.createUser(
            request.get("username"),
            request.get("password"),
            request.get("email"),
            request.get("fullName")
        );
        UserDto userDto = userService.getUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest.getUsername());
        Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
        
        if (userOpt.isEmpty() || 
            !userService.validatePassword(loginRequest.getPassword(), userOpt.get().getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        
        String token = "mock-jwt-token-" + userOpt.get().getUsername();
        userService.cacheUserSession(userOpt.get().getUsername(), token);
        return ResponseEntity.ok(new JwtResponse(token, userOpt.get().getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        log.info("Logging out user: {}", request.get("username"));
        String username = request.get("username");
        userService.logout(username);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}