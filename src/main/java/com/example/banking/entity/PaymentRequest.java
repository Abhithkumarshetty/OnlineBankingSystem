package com.example.banking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_requests")
@Data
@NoArgsConstructor
public class PaymentRequest {
    @Id
    private String paymentId;
    
    @Column(nullable = false)
    private String accountNumber;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    
    private String description;
    
    @Column(nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum PaymentType {
        DEPOSIT, WITHDRAWAL
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, SUCCESS, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}