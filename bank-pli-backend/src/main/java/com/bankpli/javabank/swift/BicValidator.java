package com.bankpli.javabank.swift;import org.springframework.stereotype.Component;
@Component public class BicValidator {public boolean isValid(String bic){String t=bic==null?"":bic.trim();return (t.length()==8||t.length()==11)&&t.substring(0,6).matches("[A-Z]{6}")&&t.substring(6).matches("[A-Z0-9]{2}([A-Z0-9]{3})?");}}
