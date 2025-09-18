package com.bankingcustomer.portal.repository;

import com.bankingcustomer.portal.entity.Account;
import com.bankingcustomer.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByIban(String iban);
    
    List<Account> findByUser(User user);
    
    List<Account> findByUserId(Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    List<Account> findActiveAccountsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.accountType = :accountType")
    List<Account> findByAccountType(@Param("accountType") Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.isActive = true")
    List<Account> findAllActiveAccounts();
    
    boolean existsByIban(String iban);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    long countAccountsByUserId(@Param("userId") Long userId);
}