package com.bankingcustomer.portal.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    
    private Long id;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime transactionDate;
    private BigDecimal fee;
    private String referenceNumber;
    private String description;
    private BigDecimal balanceAfter;
    private String accountIban;
    
    // Constructors
    public TransactionDto() {}
    
    public TransactionDto(Long id, BigDecimal amount, String transactionType, LocalDateTime transactionDate,
                         BigDecimal fee, String referenceNumber, String description, BigDecimal balanceAfter,
                         String accountIban) {
        this.id = id;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.fee = fee;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.accountIban = accountIban;
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
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public BigDecimal getFee() {
        return fee;
    }
    
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public String getAccountIban() {
        return accountIban;
    }
    
    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }
}