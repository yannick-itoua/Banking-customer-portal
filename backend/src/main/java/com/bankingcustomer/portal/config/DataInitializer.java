package com.bankingcustomer.portal.config;

import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.service.AccountService;
import com.bankingcustomer.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (!userService.existsByUsername("admin")) {
            // Create admin user
            User admin = new User("admin", "admin123", "admin@bank.com", "Admin", "User", User.Role.ADMIN);
            userService.createUser(admin);
            System.out.println("Created admin user: admin/admin123");
        }
        
        // Check if client user exists
        if (!userService.existsByUsername("client")) {
            // Create client user
            User client = new User("client", "client123", "client@example.com", "John", "Doe", User.Role.CLIENT);
            User savedClient = userService.createUser(client);
            
            // Create accounts for client
            Account checkingAccount = new Account(
                accountService.generateIban(),
                "John's Checking Account",
                Account.AccountType.CHECKING,
                savedClient
            );
            checkingAccount.setBalance(new BigDecimal("1500.00"));
            accountService.createAccount(checkingAccount);
            
            Account savingsAccount = new Account(
                accountService.generateIban(),
                "John's Savings Account",
                Account.AccountType.SAVINGS,
                savedClient
            );
            savingsAccount.setBalance(new BigDecimal("5000.00"));
            accountService.createAccount(savingsAccount);
            
            System.out.println("Created client user: client/client123 with two accounts");
        }
        
        // Create another client for testing transfers
        if (!userService.existsByUsername("jane")) {
            User jane = new User("jane", "jane123", "jane@example.com", "Jane", "Smith", User.Role.CLIENT);
            User savedJane = userService.createUser(jane);
            
            Account janeAccount = new Account(
                accountService.generateIban(),
                "Jane's Checking Account",
                Account.AccountType.CHECKING,
                savedJane
            );
            janeAccount.setBalance(new BigDecimal("2000.00"));
            accountService.createAccount(janeAccount);
            
            System.out.println("Created client user: jane/jane123 with one account");
        }
    }
}