package com.bankpli.javabank.customer;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column(nullable = false) private String email;
    @Column(nullable = false) private String phone;
    private LocalDate dateOfBirth;
    private int creditScore;
    private boolean manualReview;
    private String reviewReason;
    private Instant createdAt;

    protected Customer() {}
    public Customer(String title, String firstName, String lastName, String email, String phone, LocalDate dateOfBirth, int creditScore) {
        this.title=title; this.firstName=firstName; this.lastName=lastName; this.email=email; this.phone=phone;
        this.dateOfBirth=dateOfBirth; this.creditScore=creditScore; this.createdAt=Instant.now();
    }
    public Long getId(){return id;} public String getTitle(){return title;} public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;} public String getEmail(){return email;} public String getPhone(){return phone;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public int getCreditScore(){return creditScore;}
    public boolean isManualReview(){return manualReview;} public String getReviewReason(){return reviewReason;} public Instant getCreatedAt(){return createdAt;}
    public void update(String title,String firstName,String lastName,String email,String phone,LocalDate dob,int score){this.title=title;this.firstName=firstName;this.lastName=lastName;this.email=email;this.phone=phone;this.dateOfBirth=dob;this.creditScore=score;}
    public void flag(String reason){manualReview=true;reviewReason=reason;}
}
