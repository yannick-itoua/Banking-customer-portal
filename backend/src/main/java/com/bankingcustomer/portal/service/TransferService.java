package com.bankingcustomer.portal.service;

import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.Transaction;
import com.bankingcustomer.portal.entity.Transfer;
import com.bankingcustomer.portal.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransferService {
    
    @Autowired
    private TransferRepository transferRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionService transactionService;
    
    public List<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }
    
    public Optional<Transfer> getTransferById(Long id) {
        return transferRepository.findById(id);
    }
    
    public List<Transfer> getTransfersByAccountId(Long accountId) {
        return transferRepository.findByAccountId(accountId);
    }
    
    public Page<Transfer> getTransfersByUserId(Long userId, Pageable pageable) {
        return transferRepository.findByUserId(userId, pageable);
    }
    
    public Optional<Transfer> getTransferByReferenceNumber(String referenceNumber) {
        return transferRepository.findByReferenceNumber(referenceNumber);
    }
    
    public List<Transfer> getTransfersByStatus(Transfer.TransferStatus status) {
        return transferRepository.findByStatus(status);
    }
    
    public Transfer executeTransfer(Transfer transfer) {
        // Validate transfer
        validateTransfer(transfer);
        
        // Get accounts
        Account fromAccount = accountService.getAccountByIban(transfer.getFromIban())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + transfer.getFromIban()));
        
        Optional<Account> toAccountOpt = accountService.getAccountByIban(transfer.getToIban());
        
        // Calculate fee
        BigDecimal transferFee = transactionService.calculateTransactionFee(transfer.getAmount());
        transfer.setTransferFee(transferFee);
        transfer.setFromAccount(fromAccount);
        
        if (toAccountOpt.isPresent()) {
            transfer.setToAccount(toAccountOpt.get());
        }
        
        // Check sufficient balance (amount + fee)
        BigDecimal totalDebit = transfer.getAmount().add(transferFee);
        if (!accountService.hasSufficientBalance(transfer.getFromIban(), totalDebit)) {
            transfer.setStatus(Transfer.TransferStatus.FAILED);
            Transfer savedTransfer = transferRepository.save(transfer);
            throw new IllegalArgumentException("Insufficient balance for transfer. Required: " + totalDebit + ", Available: " + fromAccount.getBalance());
        }
        
        try {
            // Save transfer as pending first
            transfer.setStatus(Transfer.TransferStatus.PENDING);
            Transfer savedTransfer = transferRepository.save(transfer);
            
            // Create debit transaction for sender (amount + fee)
            Transaction debitTransaction = new Transaction(
                transfer.getAmount(),
                Transaction.TransactionType.TRANSFER_OUT,
                fromAccount,
                "Transfer to " + transfer.getToIban() + " - " + transfer.getDescription()
            );
            debitTransaction.setTransfer(savedTransfer);
            transactionService.createTransaction(debitTransaction);
            
            // Create fee transaction if fee > 0
            if (transferFee.compareTo(BigDecimal.ZERO) > 0) {
                Transaction feeTransaction = new Transaction(
                    transferFee,
                    Transaction.TransactionType.FEE,
                    fromAccount,
                    "Transfer fee for " + transfer.getReferenceNumber()
                );
                feeTransaction.setTransfer(savedTransfer);
                transactionService.createTransaction(feeTransaction);
            }
            
            // Create credit transaction for receiver (if internal account)
            if (toAccountOpt.isPresent()) {
                Transaction creditTransaction = new Transaction(
                    transfer.getAmount(),
                    Transaction.TransactionType.TRANSFER_IN,
                    toAccountOpt.get(),
                    "Transfer from " + transfer.getFromIban() + " - " + transfer.getDescription()
                );
                creditTransaction.setTransfer(savedTransfer);
                transactionService.createTransaction(creditTransaction);
            }
            
            // Update transfer status
            savedTransfer.setStatus(Transfer.TransferStatus.COMPLETED);
            savedTransfer.setProcessedAt(LocalDateTime.now());
            
            return transferRepository.save(savedTransfer);
            
        } catch (Exception e) {
            // Mark transfer as failed
            transfer.setStatus(Transfer.TransferStatus.FAILED);
            transferRepository.save(transfer);
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }
    
    private void validateTransfer(Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer cannot be null");
        }
        
        if (transfer.getAmount() == null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (transfer.getFromIban() == null || transfer.getFromIban().trim().isEmpty()) {
            throw new IllegalArgumentException("Source IBAN cannot be empty");
        }
        
        if (transfer.getToIban() == null || transfer.getToIban().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination IBAN cannot be empty");
        }
        
        if (transfer.getFromIban().equals(transfer.getToIban())) {
            throw new IllegalArgumentException("Source and destination IBAN cannot be the same");
        }
        
        // Validate source account exists and is active
        Account fromAccount = accountService.getAccountByIban(transfer.getFromIban())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + transfer.getFromIban()));
        
        if (!accountService.isAccountActive(transfer.getFromIban())) {
            throw new IllegalArgumentException("Source account is not active: " + transfer.getFromIban());
        }
        
        // Validate IBAN format (basic validation)
        if (!isValidIbanFormat(transfer.getFromIban()) || !isValidIbanFormat(transfer.getToIban())) {
            throw new IllegalArgumentException("Invalid IBAN format");
        }
    }
    
    private boolean isValidIbanFormat(String iban) {
        // Basic IBAN format validation
        if (iban == null || iban.length() < 15 || iban.length() > 34) {
            return false;
        }
        
        // Check if first 2 characters are letters (country code)
        String countryCode = iban.substring(0, 2);
        if (!countryCode.matches("[A-Z]{2}")) {
            return false;
        }
        
        // Check if next 2 characters are digits (check digits)
        String checkDigits = iban.substring(2, 4);
        if (!checkDigits.matches("[0-9]{2}")) {
            return false;
        }
        
        return true;
    }
    
    public Transfer cancelTransfer(Long transferId, String reason) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found with id: " + transferId));
        
        if (transfer.getStatus() != Transfer.TransferStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel transfer with status: " + transfer.getStatus());
        }
        
        transfer.setStatus(Transfer.TransferStatus.CANCELLED);
        transfer.setProcessedAt(LocalDateTime.now());
        
        return transferRepository.save(transfer);
    }
    
    public List<Transfer> getPendingTransfers() {
        return transferRepository.findByStatus(Transfer.TransferStatus.PENDING);
    }
    
    public List<Transfer> getTransfersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transferRepository.findByDateRange(startDate, endDate);
    }
    
    public long getTransferCount(Long userId) {
        return transferRepository.countTransfersByUserId(userId);
    }
}