package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_request_logs")
@Data
@NoArgsConstructor
public class PaymentRequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String paymentId;
    
    @Enumerated(EnumType.STRING)
    private PaymentRequest.PaymentStatus oldStatus;
    
    @Enumerated(EnumType.STRING)
    private PaymentRequest.PaymentStatus newStatus;
    
    private String message;
    
    private String transactionId;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    public PaymentRequestLog(String paymentId, PaymentRequest.PaymentStatus oldStatus, 
                           PaymentRequest.PaymentStatus newStatus, String message, String transactionId) {
        this.paymentId = paymentId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }
}