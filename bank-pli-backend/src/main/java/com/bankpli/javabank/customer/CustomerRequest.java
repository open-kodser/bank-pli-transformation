package com.bankpli.javabank.customer;

import java.time.LocalDate;

public record CustomerRequest(String title, String firstName, String lastName, String email, String phone,
                              LocalDate dateOfBirth, int creditScore) {}
