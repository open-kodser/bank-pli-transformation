package com.bankpli.javabank.account;
import java.math.*; import org.springframework.stereotype.Service;
@Service public class InterestService {public BigDecimal accrue(BigDecimal balance,BigDecimal annualRate,long days){if(balance==null||annualRate==null)throw new IllegalArgumentException("Balance and rate are required");if(days<=0)return BigDecimal.ZERO.setScale(2);return balance.multiply(annualRate).multiply(BigDecimal.valueOf(days)).divide(BigDecimal.valueOf(365),2,RoundingMode.HALF_UP);}}
