package com.bankpli.javabank.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BankProperties.class)
public class BankConfiguration {
    @Bean(destroyMethod = "shutdown") Executor bureauExecutor() { return Executors.newFixedThreadPool(5); }
}
