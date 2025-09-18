package com.bankingcustomer.portal.service;

import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.User;
import com.bankingcustomer.portal.repository.AccountRepository;
import com.bankingcustomer.portal.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    public Optional<Account> getAccountByIban(String iban) {
        return accountRepository.findByIban(iban);
    }
    
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }
    
    public List<Account> getActiveAccountsByUserId(Long userId) {
        return accountRepository.findActiveAccountsByUserId(userId);
    }
    
    public Account createAccount(Account account) {
        if (accountRepository.existsByIban(account.getIban())) {
            throw new IllegalArgumentException("Account with IBAN " + account.getIban() + " already exists");
        }
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Account account) {
        if (account.getId() == null) {
            throw new IllegalArgumentException("Account ID cannot be null for update");
        }
        return accountRepository.save(account);
    }
    
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
        
        // Check if account has pending transactions
        long transactionCount = transactionRepository.countTransactionsByAccountId(id);
        if (transactionCount > 0) {
            // Instead of deleting, deactivate the account
            account.setIsActive(false);
            accountRepository.save(account);
        } else {
            accountRepository.deleteById(id);
        }
    }
    
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        return account.getBalance();
    }
    
    public BigDecimal getAccountBalance(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with IBAN: " + iban));
        return account.getBalance();
    }
    
    public void updateAccountBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    public void updateAccountBalance(String iban, BigDecimal newBalance) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with IBAN: " + iban));
        account.setBalance(newBalance);
        accountRepository.save(account);
    }
    
    public boolean hasAccount(User user, String iban) {
        List<Account> userAccounts = accountRepository.findByUser(user);
        return userAccounts.stream().anyMatch(account -> account.getIban().equals(iban));
    }
    
    public boolean isAccountActive(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with IBAN: " + iban));
        return account.getIsActive();
    }
    
    public boolean hasSufficientBalance(String iban, BigDecimal amount) {
        BigDecimal balance = getAccountBalance(iban);
        return balance.compareTo(amount) >= 0;
    }
    
    public String generateIban() {
        // Simple IBAN generation for demonstration purposes
        // In a real application, this would follow proper IBAN standards
        String countryCode = "FR";
        String checkDigits = "14";
        String bankCode = "2004";
        String branchCode = "1000";
        String accountNumber = String.format("%011d", (long)(Math.random() * 99999999999L));
        
        return countryCode + checkDigits + bankCode + branchCode + accountNumber;
    }
}