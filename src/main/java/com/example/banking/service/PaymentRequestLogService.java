package com.example.banking.service;

import com.example.banking.entity.PaymentRequest;
import com.example.banking.entity.PaymentRequestLog;
import com.example.banking.repository.PaymentRequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestLogService {
    
    @Autowired
    private PaymentRequestLogRepository paymentRequestLogRepository;
    
    public void logStatusChange(String paymentId, PaymentRequest.PaymentStatus oldStatus, 
                               PaymentRequest.PaymentStatus newStatus, String message, String transactionId) {
        PaymentRequestLog log = new PaymentRequestLog(paymentId, oldStatus, newStatus, message, transactionId);
        paymentRequestLogRepository.save(log);
    }
}