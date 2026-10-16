package com.bankpli.javabank.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="accounts",uniqueConstraints=@UniqueConstraint(columnNames={"accountNumber","sortCode"}))
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long customerId;
    @Column(nullable=false,length=20) private String accountNumber;
    @Column(nullable=false,length=6) private String sortCode;
    @Enumerated(EnumType.STRING) private AccountType type;
    @Column(precision=15,scale=2,nullable=false) private BigDecimal currentBalance=BigDecimal.ZERO;
    @Column(precision=15,scale=2,nullable=false) private BigDecimal availableBalance=BigDecimal.ZERO;
    @Column(precision=15,scale=2,nullable=false) private BigDecimal overdraftLimit=BigDecimal.ZERO;
    @Column(precision=9,scale=6,nullable=false) private BigDecimal interestRate=BigDecimal.ZERO;
    private boolean active=true; private LocalDate openedDate; private LocalDate lastInterestDate; private LocalDate nextStatementDate; private int statementCycleDay;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="account_holders",joinColumns=@JoinColumn(name="account_id")) @Column(name="customer_id") private Set<Long> holderIds=new LinkedHashSet<>();
    protected Account(){}
    public Account(Long customerId,String number,String sort,AccountType type,BigDecimal opening,BigDecimal overdraft,BigDecimal rate){this.customerId=customerId;this.accountNumber=number;this.sortCode=sort;this.type=type;this.currentBalance=opening;this.availableBalance=opening;this.overdraftLimit=overdraft;this.interestRate=rate;this.openedDate=LocalDate.now();this.lastInterestDate=openedDate;this.statementCycleDay=(int)(customerId%28)+1;this.nextStatementDate=openedDate.plusDays(30);this.holderIds.add(customerId);}
    public Long getId(){return id;} public Long getCustomerId(){return customerId;} public String getAccountNumber(){return accountNumber;} public String getSortCode(){return sortCode;} public AccountType getType(){return type;} public BigDecimal getCurrentBalance(){return currentBalance;} public BigDecimal getAvailableBalance(){return availableBalance;} public BigDecimal getOverdraftLimit(){return overdraftLimit;} public BigDecimal getInterestRate(){return interestRate;} public boolean isActive(){return active;} public LocalDate getOpenedDate(){return openedDate;} public LocalDate getLastInterestDate(){return lastInterestDate;} public LocalDate getNextStatementDate(){return nextStatementDate;} public int getStatementCycleDay(){return statementCycleDay;} public Set<Long> getHolderIds(){return Set.copyOf(holderIds);}
    public void debit(BigDecimal amount){currentBalance=currentBalance.subtract(amount);availableBalance=availableBalance.subtract(amount);} public void credit(BigDecimal amount){currentBalance=currentBalance.add(amount);availableBalance=availableBalance.add(amount);} public void deactivate(){active=false;} public void addHolder(long customerId){holderIds.add(customerId);} public void removeHolder(long customerId){if(holderIds.size()<=1)throw new IllegalStateException("Account must have a holder");holderIds.remove(customerId);}
}
