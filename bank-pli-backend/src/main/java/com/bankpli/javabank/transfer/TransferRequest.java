package com.bankpli.javabank.transfer;import java.math.BigDecimal;import java.time.LocalDate;
public record TransferRequest(String fromAccount,String fromSortCode,String toAccount,String toSortCode,BigDecimal amount,Priority priority,TransferType type,LocalDate scheduledDate,boolean externalBankVerified,boolean wireAuthorized){}
