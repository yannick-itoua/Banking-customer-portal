package com.bankingcustomer.portal.repository;

import com.bankingcustomer.portal.entity.Transfer;
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
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    
    List<Transfer> findByFromAccount(Account fromAccount);
    
    List<Transfer> findByToAccount(Account toAccount);
    
    @Query("SELECT t FROM Transfer t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.createdAt DESC")
    List<Transfer> findByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transfer t WHERE t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId ORDER BY t.createdAt DESC")
    Page<Transfer> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    Optional<Transfer> findByReferenceNumber(String referenceNumber);
    
    @Query("SELECT t FROM Transfer t WHERE t.status = :status")
    List<Transfer> findByStatus(@Param("status") Transfer.TransferStatus status);
    
    @Query("SELECT t FROM Transfer t WHERE t.fromIban = :iban OR t.toIban = :iban ORDER BY t.createdAt DESC")
    List<Transfer> findByIban(@Param("iban") String iban);
    
    @Query("SELECT t FROM Transfer t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transfer> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Transfer t WHERE t.fromAccount.user.id = :userId")
    long countTransfersByUserId(@Param("userId") Long userId);
}