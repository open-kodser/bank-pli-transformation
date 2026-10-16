package com.bankpli.javabank.account;
import java.math.BigDecimal;
public record AccountRequest(Long customerId,AccountType type,BigDecimal openingBalance,BigDecimal overdraftLimit,BigDecimal interestRate){}
