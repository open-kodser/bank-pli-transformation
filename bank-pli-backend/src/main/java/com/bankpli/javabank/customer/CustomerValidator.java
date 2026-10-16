package com.bankpli.javabank.customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {
    public List<String> validate(CustomerRequest c) {
        List<String> errors = new ArrayList<>();
        if (c == null) return List.of("Customer is required");
        if (blank(c.firstName())) errors.add("First name is required");
        else if (!c.firstName().matches("[A-Za-z]+")) errors.add("First name must contain only letters");
        if (blank(c.lastName())) errors.add("Last name is required");
        else if (!c.lastName().matches("[A-Za-z]+")) errors.add("Last name must contain only letters");
        if (blank(c.email()) || c.email().indexOf('@') <= 0 || c.email().indexOf('.', c.email().indexOf('@')) < 0) errors.add("Invalid email format");
        String digits = c.phone() == null ? "" : c.phone().replaceAll("[()\\-. ]", "");
        if (!digits.matches("\\d{10,14}")) errors.add("Phone must contain 10 to 14 digits");
        if (c.dateOfBirth() == null || !c.dateOfBirth().isBefore(LocalDate.now())) errors.add("Date of birth must be in the past");
        if (c.creditScore() < 0 || c.creditScore() > 850) errors.add("Credit score must be between 0 and 850");
        return errors;
    }
    private boolean blank(String value){ return value == null || value.trim().isEmpty(); }
}
