package com.bankingcustomer.portal.controller;

import com.bankingcustomer.portal.dto.transfer.TransferRequest;
import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.Transfer;
import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.service.AccountService;
import com.bankingcustomer.portal.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransferController {
    
    @Autowired
    private TransferService transferService;
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping("/execute")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> executeTransfer(@Valid @RequestBody TransferExecutionRequest request,
                                            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            // Verify that the from account belongs to the authenticated user (unless admin)
            Account fromAccount = accountService.getAccountByIban(request.getFromIban())
                .orElseThrow(() -> new RuntimeException("Source account not found"));
            
            if (!user.getRole().equals(User.Role.ADMIN) && !fromAccount.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied - You can only transfer from your own accounts");
            }
            
            // Create transfer object
            Transfer transfer = new Transfer(
                request.getAmount(),
                request.getFromIban(),
                request.getToIban(),
                request.getBeneficiaryName(),
                request.getDescription(),
                fromAccount
            );
            
            Transfer executedTransfer = transferService.executeTransfer(transfer);
            return ResponseEntity.ok(convertToDto(executedTransfer));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not execute transfer - " + e.getMessage());
        }
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserTransfers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page, size);
            Page<Transfer> transfers = transferService.getTransfersByUserId(user.getId(), pageable);
            
            Page<TransferDto> transferDtos = transfers.map(this::convertToDto);
            
            return ResponseEntity.ok(transferDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transfers - " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransferById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Transfer transfer = transferService.getTransferById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
            
            // Check if the transfer belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && 
                !transfer.getFromAccount().getUser().getId().equals(user.getId()) &&
                (transfer.getToAccount() == null || !transfer.getToAccount().getUser().getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(transfer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transfer - " + e.getMessage());
        }
    }
    
    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransferByReference(@PathVariable String referenceNumber,
                                                   Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Transfer transfer = transferService.getTransferByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
            
            // Check if the transfer belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && 
                !transfer.getFromAccount().getUser().getId().equals(user.getId()) &&
                (transfer.getToAccount() == null || !transfer.getToAccount().getUser().getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(transfer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transfer - " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelTransfer(@PathVariable Long id,
                                           @RequestParam String reason,
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Transfer transfer = transferService.getTransferById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
            
            // Check if the transfer belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && 
                !transfer.getFromAccount().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied - You can only cancel your own transfers");
            }
            
            Transfer cancelledTransfer = transferService.cancelTransfer(id, reason);
            return ResponseEntity.ok(convertToDto(cancelledTransfer));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not cancel transfer - " + e.getMessage());
        }
    }
    
    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTransfers() {
        try {
            List<Transfer> transfers = transferService.getAllTransfers();
            List<TransferDto> transferDtos = transfers.stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(transferDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transfers - " + e.getMessage());
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingTransfers() {
        try {
            List<Transfer> transfers = transferService.getPendingTransfers();
            List<TransferDto> transferDtos = transfers.stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(transferDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve pending transfers - " + e.getMessage());
        }
    }
    
    private TransferDto convertToDto(Transfer transfer) {
        return new TransferDto(
            transfer.getId(),
            transfer.getAmount(),
            transfer.getTransferFee(),
            transfer.getFromIban(),
            transfer.getToIban(),
            transfer.getBeneficiaryName(),
            transfer.getDescription(),
            transfer.getReferenceNumber(),
            transfer.getStatus().name(),
            transfer.getCreatedAt(),
            transfer.getProcessedAt()
        );
    }
    
    // Inner classes for request/response
    public static class TransferExecutionRequest extends TransferRequest {
        private String fromIban;
        
        public String getFromIban() {
            return fromIban;
        }
        
        public void setFromIban(String fromIban) {
            this.fromIban = fromIban;
        }
    }
    
    public static class TransferDto {
        private Long id;
        private java.math.BigDecimal amount;
        private java.math.BigDecimal transferFee;
        private String fromIban;
        private String toIban;
        private String beneficiaryName;
        private String description;
        private String referenceNumber;
        private String status;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime processedAt;
        
        public TransferDto(Long id, java.math.BigDecimal amount, java.math.BigDecimal transferFee,
                          String fromIban, String toIban, String beneficiaryName,
                          String description, String referenceNumber, String status,
                          java.time.LocalDateTime createdAt, java.time.LocalDateTime processedAt) {
            this.id = id;
            this.amount = amount;
            this.transferFee = transferFee;
            this.fromIban = fromIban;
            this.toIban = toIban;
            this.beneficiaryName = beneficiaryName;
            this.description = description;
            this.referenceNumber = referenceNumber;
            this.status = status;
            this.createdAt = createdAt;
            this.processedAt = processedAt;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        
        public java.math.BigDecimal getTransferFee() { return transferFee; }
        public void setTransferFee(java.math.BigDecimal transferFee) { this.transferFee = transferFee; }
        
        public String getFromIban() { return fromIban; }
        public void setFromIban(String fromIban) { this.fromIban = fromIban; }
        
        public String getToIban() { return toIban; }
        public void setToIban(String toIban) { this.toIban = toIban; }
        
        public String getBeneficiaryName() { return beneficiaryName; }
        public void setBeneficiaryName(String beneficiaryName) { this.beneficiaryName = beneficiaryName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public java.time.LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(java.time.LocalDateTime processedAt) { this.processedAt = processedAt; }
    }
}