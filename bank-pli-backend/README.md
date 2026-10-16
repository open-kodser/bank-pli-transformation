# Bank PLI Backend

Bank PLI Backend is an executable Java modernization of the Bank-PLI sample banking system. It replaces CICS routing with REST endpoints, DB2/VSAM records with JPA entities, PL/I fixed-decimal money with `BigDecimal`, and JCL build steps with Maven and CI.

## Run

Requirements: JDK 21+ and Maven 3.9+.

```bash
mvn clean verify
mvn spring-boot:run
```

Open <http://localhost:8080>.

## Database and sample data

The application uses H2, a relational SQL database, through Spring Data JPA. Development data is stored in `data/javabank.mv.db`, so it remains available after the application restarts. Hibernate updates the schema when the application starts.

On the first run, the application seeds six customers, six accounts across the checking, savings, mortgage, and credit account types, and three audit events. Seeding only runs when the customer table is empty, which prevents duplicate records on later restarts. Set `bank.seed.enabled=false` to disable it.

Automated tests use a separate in-memory H2 database and disable seeding, so test runs cannot change the development data.

## Migrated capabilities

- Customer search, create, update, termination checks, validation, and manual review
- Account creation, deactivation, joint holders, overdraft enforcement, statement cycles
- Mortgage payments and amortization, interest accrual
- Transfer validation, PL/I fee tiers, authorization flags, atomic debit/credit, routing
- Five-bureau asynchronous scoring with 3/5 quorum, score decisions, DFT trend analysis
- Append-only auditing, transaction-line parsing, and summary reports
- Batch data generation and bounded transaction-chain traversal
- BIC validation, MT103/MT202 queueing, FX conversion, Unicode currency and PL/I-style picture formatting
- Runtime configuration, KYC profile hashing, password hashing, menu dispatch, and global API errors
- A small browser operations console in `src/main/resources/static`

## API entry points

`/api/menu`, `/api/customers`, `/api/accounts`, `/api/transfers`, `/api/credit`, `/api/swift`, `/api/audit`, `/api/batch`, `/api/admin`, and `/api/health`.

## Fidelity decisions

- `BNKXFER.PLI` allows amounts above 10,000,000 when priority is High or Urgent and then applies type-specific authorization. This actual source behavior takes precedence over the contradictory cap wording in `TESTING.md`.
- The separate `MIN_CREDIT_SCORE = 600` remains the customer manual-review trigger. The credit decision engine preserves the source thresholds: 720+ approve, 650–719 conditional review, below 650 deny.
- The PL/I account, customer, and transfer numbers are represented as strings at API boundaries to preserve leading zeros.
- External-bank verification and wire authorization are explicit request flags because the legacy functions are stubs and no external providers are defined.

See [MIGRATION.md](docs/MIGRATION.md) for the feature map and remaining production integration work.
