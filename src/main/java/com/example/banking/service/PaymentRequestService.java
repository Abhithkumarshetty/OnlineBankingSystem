package com.example.banking.service;

import com.example.banking.entity.PaymentRequest;
import com.example.banking.repository.PaymentRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class PaymentRequestService {
    
    @Autowired
    private PaymentRequestRepository paymentRequestRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private PaymentRequestLogService paymentRequestLogService;
    
    public PaymentRequest createPaymentRequest(String accountNumber, BigDecimal amount, 
                                             PaymentRequest.PaymentType type, String description, Long userId) {
        String paymentId = UUID.randomUUID().toString();
        
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentId(paymentId);
        paymentRequest.setAccountNumber(accountNumber);
        paymentRequest.setAmount(amount);
        paymentRequest.setType(type);
        paymentRequest.setDescription(description);
        paymentRequest.setUserId(userId);
        paymentRequest.setStatus(PaymentRequest.PaymentStatus.PENDING);
        
        // Save to database first
        PaymentRequest savedRequest = paymentRequestRepository.save(paymentRequest);
        log.info("payment data saved in db: {}", savedRequest.toString());
        
        // Log initial creation
        paymentRequestLogService.logStatusChange(paymentId, null, 
            PaymentRequest.PaymentStatus.PENDING, "Payment request created", null);
        
        log.info("Kafka event are sending");
        // Then send to Kafka
        try {
            kafkaProducerService.sendPaymentRequest(accountNumber, amount, type.toString(), description, userId);
            PaymentRequest.PaymentStatus oldStatus = savedRequest.getStatus();
            savedRequest.setStatus(PaymentRequest.PaymentStatus.PROCESSING);
            paymentRequestRepository.save(savedRequest);
            
            // Log status change
            paymentRequestLogService.logStatusChange(paymentId, oldStatus, 
                PaymentRequest.PaymentStatus.PROCESSING, "Sent to payment service", null);
                
        } catch (Exception e) {
            PaymentRequest.PaymentStatus oldStatus = savedRequest.getStatus();
            savedRequest.setStatus(PaymentRequest.PaymentStatus.FAILED);
            paymentRequestRepository.save(savedRequest);
            
            // Log failure
            paymentRequestLogService.logStatusChange(paymentId, oldStatus, 
                PaymentRequest.PaymentStatus.FAILED, "Failed to send to payment service: " + e.getMessage(), null);
        }
        
        return savedRequest;
    }
}