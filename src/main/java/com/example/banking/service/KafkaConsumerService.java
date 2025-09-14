package com.example.banking.service;

import com.example.banking.entity.PaymentRequest;
import com.example.banking.repository.PaymentRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class KafkaConsumerService {
    
    @Autowired
    private PaymentRequestRepository paymentRequestRepository;
    
    @Autowired
    private PaymentRequestLogService paymentRequestLogService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "payment-response", groupId = "banking-service")
    public void handlePaymentResponse(String message) {
        try {
            log.info("Received payment response: {}", message);
            Map<String, Object> response = objectMapper.readValue(message, Map.class);
            String paymentId = (String) response.get("paymentId");
            String status = (String) response.get("status");
            
            log.info("Received payment response: {} - {}", paymentId, status);
            
            Optional<PaymentRequest> paymentRequestOpt = paymentRequestRepository.findById(paymentId);
            if (paymentRequestOpt.isPresent()) {
                PaymentRequest paymentRequest = paymentRequestOpt.get();
                PaymentRequest.PaymentStatus oldStatus = paymentRequest.getStatus();
                String transactionId = (String) response.get("transactionId");
                String comment = (String) response.get("message");
                
                if ("SUCCESS".equals(status)) {
                    paymentRequest.setStatus(PaymentRequest.PaymentStatus.SUCCESS);
                } else {
                    paymentRequest.setStatus(PaymentRequest.PaymentStatus.FAILED);
                }
                
                paymentRequestRepository.save(paymentRequest);
                
                // Log status change
                paymentRequestLogService.logStatusChange(paymentId, oldStatus, 
                    paymentRequest.getStatus(), comment, transactionId);
                    
                log.info("Updated payment request {} status to {}", paymentId, paymentRequest.getStatus());
            } else {
                log.warn("Payment request not found: {}", paymentId);
            }
            
        } catch (Exception e) {
            log.error("Error processing payment response: {}", e.getMessage());
        }
    }
}