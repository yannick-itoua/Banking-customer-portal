package com.bankingcustomer.portal.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {
    
    private Long id;
    private String iban;
    private BigDecimal balance;
    private String accountType;
    private String accountName;
    private LocalDateTime createdAt;
    private Boolean isActive;
    
    // Constructors
    public AccountDto() {}
    
    public AccountDto(Long id, String iban, BigDecimal balance, String accountType, 
                     String accountName, LocalDateTime createdAt, Boolean isActive) {
        this.id = id;
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.accountName = accountName;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}