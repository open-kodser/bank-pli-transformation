package com.bankpli.javabank.customer;

import com.bankpli.javabank.account.AccountRepository;
import com.bankpli.javabank.shared.BusinessException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customers; private final CustomerValidator validator; private final AccountRepository accounts;
    public CustomerService(CustomerRepository customers, CustomerValidator validator, AccountRepository accounts){this.customers=customers;this.validator=validator;this.accounts=accounts;}
    public Customer create(CustomerRequest request){validate(request);Customer c=new Customer(request.title(),request.firstName(),request.lastName(),request.email(),request.phone(),request.dateOfBirth(),request.creditScore());if(request.creditScore()<600)c.flag("Low credit score");return customers.save(c);}
    public Customer update(long id,CustomerRequest request){validate(request);Customer c=get(id);c.update(request.title(),request.firstName(),request.lastName(),request.email(),request.phone(),request.dateOfBirth(),request.creditScore());if(request.creditScore()<600)c.flag("Low credit score");return c;}
    public Customer get(long id){return customers.findById(id).orElseThrow(()->new BusinessException(HttpStatus.NOT_FOUND,"Customer not found"));}
    @Transactional(readOnly=true) public Page<Customer> search(String query,int page,int size){String q=query==null?"":query;return customers.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(q,q,PageRequest.of(page,size));}
    public void delete(long id){get(id);if(accounts.existsByCustomerIdAndActiveTrue(id))throw new BusinessException(HttpStatus.CONFLICT,"Customer has open accounts");customers.deleteById(id);}
    public Customer flag(long id,String reason){Customer c=get(id);c.flag(reason);return c;}
    private void validate(CustomerRequest r){List<String> errors=validator.validate(r);if(!errors.isEmpty())throw new BusinessException(HttpStatus.BAD_REQUEST,errors.get(0));}
}
