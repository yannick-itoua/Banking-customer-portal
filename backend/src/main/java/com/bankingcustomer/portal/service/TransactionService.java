package com.bankingcustomer.portal.service;

import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.Transaction;
import com.bankingcustomer.portal.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;
    
    private static final BigDecimal TRANSACTION_FEE_RATE = new BigDecimal("0.005"); // 0.5%
    private static final BigDecimal MIN_FEE = new BigDecimal("0.10");
    private static final BigDecimal MAX_FEE = new BigDecimal("10.00");
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public List<Transaction> getTransactionsByAccount(Account account) {
        return transactionRepository.findByAccount(account);
    }
    
    public Page<Transaction> getTransactionsByAccount(Account account, Pageable pageable) {
        return transactionRepository.findByAccountOrderByTransactionDateDesc(account, pageable);
    }
    
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
    }
    
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }
    
    public Optional<Transaction> getTransactionByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber);
    }
    
    public Transaction createTransaction(Transaction transaction) {
        // Calculate fee if it's a transfer transaction
        if (transaction.getTransactionType() == Transaction.TransactionType.TRANSFER_OUT) {
            BigDecimal fee = calculateTransactionFee(transaction.getAmount());
            transaction.setFee(fee);
        }
        
        // Update account balance
        Account account = transaction.getAccount();
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;
        
        switch (transaction.getTransactionType()) {
            case CREDIT:
            case TRANSFER_IN:
                newBalance = currentBalance.add(transaction.getAmount());
                break;
            case DEBIT:
            case TRANSFER_OUT:
                BigDecimal totalDebit = transaction.getAmount().add(transaction.getFee());
                if (currentBalance.compareTo(totalDebit) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for transaction");
                }
                newBalance = currentBalance.subtract(totalDebit);
                break;
            case FEE:
                newBalance = currentBalance.subtract(transaction.getAmount());
                break;
            default:
                newBalance = currentBalance;
        }
        
        transaction.setBalanceAfter(newBalance);
        
        // Save transaction first
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update account balance
        accountService.updateAccountBalance(account.getId(), newBalance);
        
        return savedTransaction;
    }
    
    public BigDecimal calculateTransactionFee(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal fee = amount.multiply(TRANSACTION_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        
        // Apply min and max fee limits
        if (fee.compareTo(MIN_FEE) < 0) {
            fee = MIN_FEE;
        } else if (fee.compareTo(MAX_FEE) > 0) {
            fee = MAX_FEE;
        }
        
        return fee;
    }
    
    public Transaction createCreditTransaction(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(amount, Transaction.TransactionType.CREDIT, account, description);
        return createTransaction(transaction);
    }
    
    public Transaction createDebitTransaction(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(amount, Transaction.TransactionType.DEBIT, account, description);
        return createTransaction(transaction);
    }
    
    public Transaction createFeeTransaction(Account account, BigDecimal feeAmount, String description) {
        Transaction transaction = new Transaction(feeAmount, Transaction.TransactionType.FEE, account, description);
        transaction.setFee(BigDecimal.ZERO); // Fee transactions don't have additional fees
        return createTransaction(transaction);
    }
    
    public void deleteTransaction(Long id) {
        // In a real banking system, transactions should not be deleted
        // Instead, they should be reversed with a compensating transaction
        throw new UnsupportedOperationException("Transactions cannot be deleted. Use reversal transactions instead.");
    }
    
    public Transaction reverseTransaction(Long transactionId, String reason) {
        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));
        
        // Create reverse transaction
        Transaction.TransactionType reverseType;
        switch (originalTransaction.getTransactionType()) {
            case CREDIT:
                reverseType = Transaction.TransactionType.DEBIT;
                break;
            case DEBIT:
                reverseType = Transaction.TransactionType.CREDIT;
                break;
            case TRANSFER_IN:
                reverseType = Transaction.TransactionType.TRANSFER_OUT;
                break;
            case TRANSFER_OUT:
                reverseType = Transaction.TransactionType.TRANSFER_IN;
                break;
            default:
                throw new IllegalArgumentException("Cannot reverse transaction of type: " + originalTransaction.getTransactionType());
        }
        
        Transaction reverseTransaction = new Transaction(
            originalTransaction.getAmount(),
            reverseType,
            originalTransaction.getAccount(),
            "Reversal of transaction " + originalTransaction.getReferenceNumber() + ": " + reason
        );
        
        return createTransaction(reverseTransaction);
    }
    
    public long getTransactionCount(Long accountId) {
        return transactionRepository.countTransactionsByAccountId(accountId);
    }
}