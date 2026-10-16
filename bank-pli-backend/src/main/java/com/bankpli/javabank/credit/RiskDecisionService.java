package com.bankpli.javabank.credit;import org.springframework.stereotype.Service;
@Service public class RiskDecisionService {public CreditDecision decide(int score){if(score>=720)return CreditDecision.APPROVE;if(score>=650)return CreditDecision.CONDITIONAL_REVIEW;return CreditDecision.DENY;}}
