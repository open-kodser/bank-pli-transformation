package com.bankpli.javabank.config;

import com.bankpli.javabank.account.AccountRequest;
import com.bankpli.javabank.account.AccountService;
import com.bankpli.javabank.account.AccountType;
import com.bankpli.javabank.audit.AuditService;
import com.bankpli.javabank.customer.Customer;
import com.bankpli.javabank.customer.CustomerRepository;
import com.bankpli.javabank.customer.CustomerRequest;
import com.bankpli.javabank.customer.CustomerService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "bank.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseSeeder implements ApplicationRunner {
    private final CustomerRepository customers;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final AuditService auditService;

    public DatabaseSeeder(CustomerRepository customers, CustomerService customerService,
                          AccountService accountService, AuditService auditService) {
        this.customers = customers;
        this.customerService = customerService;
        this.accountService = accountService;
        this.auditService = auditService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (customers.count() > 0) return;

        Customer grace = customer("Ms", "Grace", "Hopper", "grace.hopper@javabank.test", "(555) 101-1001", "1906-12-09", 785);
        Customer alan = customer("Mr", "Alan", "Turing", "alan.turing@javabank.test", "(555) 101-1002", "1912-06-23", 742);
        Customer katherine = customer("Dr", "Katherine", "Johnson", "katherine.johnson@javabank.test", "(555) 101-1003", "1918-08-26", 718);
        Customer margaret = customer("Dr", "Margaret", "Hamilton", "margaret.hamilton@javabank.test", "(555) 101-1004", "1936-08-17", 690);
        Customer edsger = customer("Dr", "Edsger", "Dijkstra", "edsger.dijkstra@javabank.test", "(555) 101-1005", "1930-05-11", 645);
        customer("Ms", "Ada", "Lovelace", "ada.lovelace@javabank.test", "(555) 101-1006", "1985-12-10", 580);

        account(grace, AccountType.CHECKING, "24500.00", "2500.00", "0.005");
        account(grace, AccountType.SAVINGS, "68250.50", "0.00", "0.035");
        account(alan, AccountType.CHECKING, "12340.75", "1500.00", "0.005");
        account(katherine, AccountType.SAVINGS, "45100.00", "0.00", "0.035");
        account(margaret, AccountType.MORTGAGE, "0.00", "0.00", "0.068");
        account(edsger, AccountType.CREDIT, "2500.00", "5000.00", "0.189");

        auditService.append("SYSTEM_START", "Persistent H2 SQL database initialized");
        auditService.append("SEED_COMPLETE", "Loaded 6 customers and 6 accounts");
        auditService.append("CONFIGURATION", "Default banking rules activated");
    }

    private Customer customer(String title, String first, String last, String email, String phone,
                              String dateOfBirth, int creditScore) {
        return customerService.create(new CustomerRequest(title, first, last, email, phone,
                LocalDate.parse(dateOfBirth), creditScore));
    }

    private void account(Customer customer, AccountType type, String balance, String overdraft, String rate) {
        accountService.create(new AccountRequest(customer.getId(), type, new BigDecimal(balance),
                new BigDecimal(overdraft), new BigDecimal(rate)));
    }
}
