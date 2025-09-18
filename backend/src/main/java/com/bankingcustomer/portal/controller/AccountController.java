package com.bankingcustomer.portal.controller;

import com.bankingcustomer.portal.dto.account.AccountDto;
import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.service.AccountService;
import com.bankingcustomer.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserAccounts(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Account> accounts = accountService.getActiveAccountsByUserId(user.getId());
            
            List<AccountDto> accountDtos = accounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(accountDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve accounts - " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getAccountById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // Check if the account belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && !account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve account - " + e.getMessage());
        }
    }
    
    @GetMapping("/iban/{iban}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getAccountByIban(@PathVariable String iban, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Account account = accountService.getAccountByIban(iban)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // Check if the account belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && !account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            return ResponseEntity.ok(convertToDto(account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve account - " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getAccountBalance(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Account account = accountService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            // Check if the account belongs to the authenticated user (unless admin)
            if (!user.getRole().equals(User.Role.ADMIN) && !account.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Access denied");
            }
            
            BigDecimal balance = accountService.getAccountBalance(id);
            return ResponseEntity.ok(new BalanceResponse(balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve balance - " + e.getMessage());
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreationRequest request) {
        try {
            // Validate required fields
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest().body("Error: User ID is required");
            }
            
            // Find the user
            User user = userService.getUserById(request.getUserId())
                .orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().body("Error: User not found with ID: " + request.getUserId());
            }
            
            Account account = new Account();
            account.setIban(accountService.generateIban());
            account.setAccountName(request.getAccountName());
            account.setAccountType(Account.AccountType.valueOf(request.getAccountType()));
            account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
            account.setUser(user);  // Set the user
            
            Account savedAccount = accountService.createAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedAccount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not create account - " + e.getMessage());
        }
    }
    
    // Admin endpoints
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            List<AccountDto> accountDtos = accounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(accountDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: Could not retrieve accounts - " + e.getMessage());
        }
    }
    
    private AccountDto convertToDto(Account account) {
        return new AccountDto(
            account.getId(),
            account.getIban(),
            account.getBalance(),
            account.getAccountType().name(),
            account.getAccountName(),
            account.getCreatedAt(),
            account.getIsActive()
        );
    }
    
    // Inner classes for request/response
    public static class BalanceResponse {
        private BigDecimal balance;
        
        public BalanceResponse(BigDecimal balance) {
            this.balance = balance;
        }
        
        public BigDecimal getBalance() {
            return balance;
        }
        
        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
    }
    
    public static class AccountCreationRequest {
        private String accountName;
        private String accountType;
        private BigDecimal initialBalance;
        private Long userId;
        
        // Getters and setters
        public String getAccountName() {
            return accountName;
        }
        
        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
        
        public String getAccountType() {
            return accountType;
        }
        
        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }
        
        public BigDecimal getInitialBalance() {
            return initialBalance;
        }
        
        public void setInitialBalance(BigDecimal initialBalance) {
            this.initialBalance = initialBalance;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}