package com.example.banking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class KafkaProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String sendPaymentRequest(String accountNumber, BigDecimal amount, String type, String description, Long userId) {
        try {
            String paymentId = UUID.randomUUID().toString();
            log.info("Sending payment request: {}", paymentId);
            
            Map<String, Object> paymentRequest = Map.of(
                "paymentId", paymentId,
                "accountNumber", accountNumber,
                "amount", amount,
                "type", type,
                "description", description,
                "userId", userId,
                "timestamp", LocalDateTime.now().toString()
            );
            
            String message = objectMapper.writeValueAsString(paymentRequest);
            kafkaTemplate.send("payment-request", message);
            
            log.info("Payment request sent: {}", paymentId);
            return paymentId;
            
        } catch (Exception e) {
            log.error("Error sending payment request: {}", e.getMessage());
            throw new RuntimeException("Failed to send payment request");
        }
    }
}