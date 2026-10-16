package com.bankpli.javabank.account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account,Long>{boolean existsByCustomerIdAndActiveTrue(Long customerId);Optional<Account> findByAccountNumberAndSortCode(String number,String sortCode);}
