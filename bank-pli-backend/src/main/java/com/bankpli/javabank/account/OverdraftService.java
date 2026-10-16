package com.bankpli.javabank.account;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
@Service public class OverdraftService { public boolean withinLimit(BigDecimal available,BigDecimal debit,BigDecimal limit){if(available==null||debit==null||limit==null||debit.signum()<0)throw new IllegalArgumentException("Amounts are required and debit must be non-negative");return available.subtract(debit).compareTo(limit.negate())>=0;} }
