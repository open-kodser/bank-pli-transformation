# Migration coverage

This implementation follows the sequence in `../TESTING.md` and uses the PL/I source when the plan and source differ.

| Plan | Java implementation |
|---|---|
| Foundation and shared types | Spring Boot, JPA, H2, `BigDecimal`, global exception mapping |
| Customer 1–6 | `customer` package and `/api/customers` |
| Account 7–10 | `account` package and `/api/accounts` |
| Transfer 11–15 | `transfer` package and `/api/transfers` |
| Financial 16–18 | `MortgageCalculator`, `InterestService`, statement-cycle fields |
| Batch and credit 19–26 | `batch`, `credit`, and `audit` packages |
| SWIFT/FX/i18n 27–31 | `swift` package and `/api/swift` |
| Cross-cutting 32–37 | menu, exception handler, configuration, KYC, Maven, CI |

The browser console provides navigation and read-only inspection. Production deployment still needs an organization-selected database, identity provider, external-bank and bureau adapters, and infrastructure definitions. Those systems are not identified by the legacy project or migration plan.

## Validation

`mvn verify` runs validator and calculator boundaries, credit quorum behavior, REST contracts, and persistence-backed customer CRUD. Transfer operations use one Spring transaction, so a failed credit or audit insert rolls back the source debit.
