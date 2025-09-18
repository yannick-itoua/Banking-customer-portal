package com.bankingcustomer.portal.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Destination IBAN is required")
    private String toIban;
    
    @NotBlank(message = "Beneficiary name is required")
    private String beneficiaryName;
    
    private String description;
    
    // Constructors
    public TransferRequest() {}
    
    public TransferRequest(BigDecimal amount, String toIban, String beneficiaryName, String description) {
        this.amount = amount;
        this.toIban = toIban;
        this.beneficiaryName = beneficiaryName;
        this.description = description;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}