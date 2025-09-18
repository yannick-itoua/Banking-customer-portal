package com.bankingcustomer.portal.controller;

import com.bankingcustomer.portal.dto.transaction.TransactionDto;
import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.Transaction;
import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.service.AccountService;
import com.bankingcustomer.portal.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransactionsByAccount(@PathVariable Long accountId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Account account = accountService.getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // Check if the account belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && !account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Transaction> transactions = transactionService.getTransactionsByAccount(account, pageable);
            
            Page<TransactionDto> transactionDtos = transactions.map(this::convertToDto);
            
            return ResponseEntity.ok(transactionDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transactions - " + e.getMessage());
        }
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserTransactions(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Transaction> transactions = transactionService.getTransactionsByUserId(user.getId());
            
            List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(transactionDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transactions - " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Transaction transaction = transactionService.getTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
            // Check if the transaction belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && 
                !transaction.getAccount().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transaction - " + e.getMessage());
        }
    }
    
    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransactionByReference(@PathVariable String referenceNumber, 
                                                      Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Transaction transaction = transactionService.getTransactionByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
            // Check if the transaction belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && 
                !transaction.getAccount().getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transaction - " + e.getMessage());
        }
    }
    
    @GetMapping("/account/{accountId}/date-range")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getTransactionsByDateRange(@PathVariable Long accountId,
                                                       @RequestParam String startDate,
                                                       @RequestParam String endDate,
                                                       Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Account account = accountService.getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // Check if the account belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && !account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(accountId, start, end);
            
            List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(transactionDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transactions - " + e.getMessage());
        }
    }
    
    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(transactionDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve transactions - " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/reverse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reverseTransaction(@PathVariable Long id,
                                               @RequestParam String reason) {
        try {
            Transaction reversedTransaction = transactionService.reverseTransaction(id, reason);
            return ResponseEntity.ok(convertToDto(reversedTransaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not reverse transaction - " + e.getMessage());
        }
    }
    
    private TransactionDto convertToDto(Transaction transaction) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getTransactionType().name(),
            transaction.getTransactionDate(),
            transaction.getFee(),
            transaction.getReferenceNumber(),
            transaction.getDescription(),
            transaction.getBalanceAfter(),
            transaction.getAccount().getIban()
        );
    }
}