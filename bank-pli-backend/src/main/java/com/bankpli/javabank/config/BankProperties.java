package com.bankpli.javabank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bank")
public record BankProperties(String systemSortCode, String defaultCurrency) {}
