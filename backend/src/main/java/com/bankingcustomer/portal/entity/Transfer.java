package com.bankingcustomer.portal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "transfers")
public class Transfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Amount is required")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "transfer_fee", precision = 19, scale = 2)
    private BigDecimal transferFee = BigDecimal.ZERO;
    
    @Column(name = "from_iban", nullable = false)
    private String fromIban;
    
    @Column(name = "to_iban", nullable = false)
    private String toIban;
    
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "reference_number", unique = true)
    private String referenceNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.PENDING;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;
    
    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;
    
    // Constructors
    public Transfer() {}
    
    public Transfer(BigDecimal amount, String fromIban, String toIban, 
                   String beneficiaryName, String description, Account fromAccount) {
        this.amount = amount;
        this.fromIban = fromIban;
        this.toIban = toIban;
        this.beneficiaryName = beneficiaryName;
        this.description = description;
        this.fromAccount = fromAccount;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (referenceNumber == null) {
            referenceNumber = generateReferenceNumber();
        }
    }
    
    private String generateReferenceNumber() {
        return "TRF" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getTransferFee() {
        return transferFee;
    }
    
    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }
    
    public String getFromIban() {
        return fromIban;
    }
    
    public void setFromIban(String fromIban) {
        this.fromIban = fromIban;
    }
    
    public String getToIban() {
        return toIban;
    }
    
    public void setToIban(String toIban) {
        this.toIban = toIban;
    }
    
    public String getBeneficiaryName() {
        return beneficiaryName;
    }
    
    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public TransferStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransferStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public Account getFromAccount() {
        return fromAccount;
    }
    
    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public Account getToAccount() {
        return toAccount;
    }
    
    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    
    public Set<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public enum TransferStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
}