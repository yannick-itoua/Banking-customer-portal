package com.bankingcustomer.portal.repository;

import com.bankingcustomer.portal.entity.Transaction;
import com.bankingcustomer.portal.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccount(Account account);
    
    Page<Transaction> findByAccountOrderByTransactionDateDesc(Account account, Pageable pageable);
    
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionType = :transactionType")
    List<Transaction> findByTransactionType(@Param("transactionType") Transaction.TransactionType transactionType);
    
    @Query("SELECT t FROM Transaction t WHERE t.transfer.id = :transferId")
    List<Transaction> findByTransferId(@Param("transferId") Long transferId);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.id = :accountId")
    long countTransactionsByAccountId(@Param("accountId") Long accountId);
}