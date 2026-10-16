# High-Fidelity Modernization Plan: Bank-PLI → Java

This is a modernization roadmap for the **Bank-PLI** legacy mainframe banking system (PL/I + JCL + DB2 + CICS + VSAM/QSAM) to a Java stack. The plan is grounded strictly in the features documented in the provided context. Every feature below maps to a documented PL/I procedure, JCL step, or business rule.

**Important grounding note:** The context provides *program/procedure summaries* (purpose, inputs, business rules, call chains) — it does **not** provide the actual PL/I source line bodies. So Java signatures, class layouts, exact numeric constants beyond those documented, and file paths below are **Proposed (not in context)** unless tied to a named identifier from the context. I flag proposed items inline.

---

## Understanding the Task

Migrate the PL/I banking suite (`BNKMAIN`, `BNKCUST`, `BNKACCT`, `BNKXFER`, `BNKCRED`, `BNKBATC`, `BNKAUDIT`, `BNKFX`) to Java, feature-by-feature, with:
1. A Java target for each documented feature.
2. The front-end features needed to support each capability.
3. Validation + documented test cases per feature.

**Assumptions:**
- Target stack (Proposed): Spring Boot (REST API), JPA/Hibernate over a relational DB replacing DB2/VSAM, React/Angular front-end replacing CICS 3270 maps. These frameworks are **not named in the source** — they are proposed defaults.
- CICS transaction dispatch (`BNKMAIN`) becomes REST routing.
- Batch (`BNKBATC`, `BNKAUDIT`, credit scoring) becomes Spring Batch or scheduled jobs.

---

## Feature Inventory (documented source → Java target)

Every row is a documented feature. "PL/I source" cites the exact procedure/step/module name from context.

| # | Feature | PL/I source (from context) | Java target module (Proposed) | Front-end needed |
|---|---------|----------------------------|-------------------------------|------------------|
| 1 | Customer inquiry/search | `BNKCUST` / `BNKCUST_ENTRY`, DB cursor fetch | `CustomerController`, `CustomerService.search()` | Customer search screen with pagination |
| 2 | Customer create | `CREATE_CUSTOMER_RECORD`, `VALIDATE_CUSTOMER_DATA` | `CustomerService.create()` | Customer create form |
| 3 | Customer update | `UPDATE_CUSTOMER_RECORD`, `VALIDATE_CUSTOMER_DATA` | `CustomerService.update()` | Customer edit form |
| 4 | Customer delete/terminate | `BNKCUST` termination (checks open accounts) | `CustomerService.delete()` | Delete confirm dialog |
| 5 | Customer data validation | `VALIDATE_CUSTOMER_DATA`, `VALIDATE_DATA` | `CustomerValidator` | Inline field validation + error banner |
| 6 | Manual review flagging | `FLAG_FOR_MANUAL_REVIEW` | `ManualReviewService` | Review-queue dashboard |
| 7 | Account lifecycle (create/update/deactivate/delete) | `CREATE_ACCOUNT`, Account Lifecycle Mgmt | `AccountService` | Account management screen |
| 8 | Account validation | `VALIDATE_ACCOUNTS` | `AccountValidator` | Account status display |
| 9 | Joint/holder management | Joint Account & Holder Management (linked-list) | `AccountHolderService` | Holder list + add/remove UI |
| 10 | Overdraft/limit enforcement | `CHECK_OVERDRAFT`, Overdraft & Limit Enforcement | `OverdraftService` | Overdraft warning UI |
| 11 | Transfer request validation | `VALIDATE_TRANSFER_REQUEST` | `TransferValidator` | Transfer form with rule messaging |
| 12 | Fee calculation | `CALCULATE_TRANSFER_FEE`, Tiered Fee Calculation | `FeeCalculator` | Fee preview on transfer form |
| 13 | Fund movement (atomic debit/credit) | `EXECUTE_FUND_MOVEMENT` | `FundMovementService` (transactional) | Transfer confirmation |
| 14 | Transfer routing / intermediary | `ROUTE_TRANSFER`, `REQUIRES_INTERMEDIARY`, `FIND_INTERMEDIARY` | `TransferRoutingService` | Routing path display (optional) |
| 15 | Ownership / external-bank / wire checks | `SAME_CUSTOMER_OWNERSHIP`, `VERIFY_EXTERNAL_BANK`, `CHECK_WIRE_AUTHORIZATION` | `AuthorizationService` | Auth-required prompts |
| 16 | Mortgage payment / amortization | `CALCULATE_MORTGAGE_PAYMENT`, `BUILD_AMORTIZATION_SCHEDULE` | `MortgageCalculator` | Amortization schedule table |
| 17 | Interest accrual | `CALCULATE_INTEREST_ACCRUAL` | `InterestService` | Accrued interest display |
| 18 | Statement cycle | `SET_STATEMENT_CYCLE` | `StatementCycleService` | Statement config screen |
| 19 | Batch transaction processing | `PROCESS_TRANSACTION_BATCH`, `TRAVERSE_TRANSACTION_CHAIN` | Spring Batch job | Batch job monitor |
| 20 | Batch data generation | `BNKBATCH.GENERATE`, `CREATE_SAMPLE_TRANSACTION` | `DataGeneratorJob` | Admin generation params UI |
| 21 | Credit scoring (bureau quorum) | `SCORE_CUSTOMER`, `QUERY_BUREAU`, `CALC_SCORE`, `APPLY_RULES`, `INIT_ENGINE` | `CreditScoringService` (async) | Credit decision screen |
| 22 | Composite score & trend analysis | Composite Score & Trend Analysis (complex-number DFT) | `TrendAnalyzer` | Score/trend visualization |
| 23 | Risk rule evaluation | Risk Rule Evaluation & Decisioning | `RiskDecisionService` | Approval/denial result UI |
| 24 | Audit file open/write/process | `OPEN_AUDIT_FILES`, `WRITE_TO_FILE`, `PROCESS_AUDIT_RECORDS`, `WRITE_AUDIT_RECORD` | `AuditService` (append-only) | Audit log viewer (read-only) |
| 25 | Audit line parsing | `PARSE_TRANSACTION_LINE` | `TransactionLineParser` | — (batch) |
| 26 | Report generation | `PRINT_SUMMARY_REPORT`, `WRITE_DETAIL_LINE`, `PRINT_DETAIL_PAGE_HEADER`, ENDPAGE handler | `ReportService` | Report download/print view |
| 27 | BIC/SWIFT validation | `VALIDATE_BIC` | `BicValidator` | BIC entry field validation |
| 28 | SWIFT payment processing | `PROCESS_SWIFT_PAYMENT` | `SwiftPaymentService` | SWIFT/MT103/MT202 payment form |
| 29 | FX / currency conversion | Foreign Exchange (FX) & Currency Conversion, `BNKFX` | `FxService` | Currency selector + rate display |
| 30 | Unicode/DBCS currency display | `UNICODE_DBCS_CURRENCY_DISPLAY`, `DEMONSTRATE_WIDECHAR`, `DEMONSTRATE_GRAPHIC_DBCS` | `CurrencyFormatter` (UTF-16) | Internationalized display |
| 31 | Picture-format handling | `DEMONSTRATE_PICTURE_DRIFT`, `PICFMT` | `PictureFormatter` (BigDecimal formatting) | Formatted amount display |
| 32 | Menu navigation / AID dispatch | `BNKMAIN`, `DISPLAY_MAIN_MENU`, `SELECT(WHEN/OTHERWISE)` | `MenuController` / routing | Main menu / navigation |
| 33 | SQL error handling | `%CHECK_SQL`, `SQLCA` | JPA exception mapping | Error toast/screen |
| 34 | PL/I ON-condition safety net | ON CONVERSION/SIZE/OVERFLOW/ZERODIVIDE/STRINGSIZE/SUBSCRIPTRANGE/RECORD/STORAGE/AREA/ATTENTION handlers, `ERRHAND_CLEANUP` | Global exception handler (`@ControllerAdvice`) + rollback | Global error UI |
| 35 | Config rule engine | Configuration Rule Engine (Session/System/Interest/Routing/Runtime) | `ConfigService` | Admin config screen |
| 36 | KYC / security (hashing) | KYC & Profile Validation, SHA-256 hashing | `KycService`, password hashing | KYC form + security |
| 37 | Build/bind pipeline | `COMPILE.JCL`, `BINDALL`, `RUNBATCH.JCL` | Maven/Gradle + CI/CD | — (DevOps) |

---

## High-Level Approach

- **Dispatch layer:** `BNKMAIN`'s CICS `SELECT(WHEN/OTHERWISE)` AID routing → REST controllers + a router/menu endpoint (Feature 32).
- **Data layer:** DB2/VSAM/QSAM → relational DB via JPA. VSAM keyed/direct access (archive hashing, `FIRST_TXN_OFFSET` chains in `TRAVERSE_TRANSACTION_CHAIN`) → indexed tables + FK relationships.
- **Precision:** All PL/I `FIXED DEC(15,2)` monetary fields → `BigDecimal` (never `double`) to preserve two-decimal precision mandated by the Data Integrity business rule.
- **Transactions:** PL/I savepoint/`ROLLBACK WORK` in fund movement → `@Transactional` with savepoints.
- **Async credit scoring:** PL/I detached tasks + 3/5 quorum → `CompletableFuture` with quorum wait.
- **Audit:** Append-only, tamper-evident logs (business rule: overwrites/deletes prohibited) → insert-only audit table, no update/delete DAO methods.

---

## Step-by-Step Plan (grouped by domain)

### 1. Foundation & shared types
- Model `CUSTOMER_MASTER`, `ACCTSTR`/`ACCOUNT_REC`, `TXNSTR`/`TRANSACTION_*`, `TRANSFER_REQUEST` structures as JPA entities/DTOs.
- Port `SQLCA`/`%CHECK_SQL` → JPA exception translation; port ON-condition suite → `@ControllerAdvice` global handler + `ERRHAND_CLEANUP` → transactional rollback/resource cleanup.
- **Outcome:** shared domain + error backbone before feature ports.

### 2. Customer domain (Features 1–6)
- Port `VALIDATE_CUSTOMER_DATA` exactly (rules below), then CRUD, then `FLAG_FOR_MANUAL_REVIEW`.

### 3. Account domain (Features 7–10)
- Port account lifecycle, `VALIDATE_ACCOUNTS`, holder management, `CHECK_OVERDRAFT`.

### 4. Transfer domain (Features 11–15)
- Port validation → fee → routing → fund movement, preserving the documented waterfall order (amount → priority → type → ownership → accounts → dates).

### 5. Financial calc domain (Features 16–18)
- Port mortgage/interest/statement formulas with `BigDecimal`.

### 6. Batch & credit (Features 19–26)
- Port batch processing, data generation, async scoring, risk decisioning, audit, reporting.

### 7. SWIFT/FX/i18n (Features 27–31)
- Port BIC validation, SWIFT payment, FX, DBCS/picture formatting.

### 8. Cross-cutting (Features 32–37)
- Menu routing, config engine, KYC/security, build pipeline.

---

## Feature Detail: Validation Rules + Java Target + Test Cases

Below are the features whose rules are **explicitly documented**, so they can be ported and tested with fidelity. Rules quoted are from the context.

### Feature 27 — BIC/SWIFT Validation (`VALIDATE_BIC`)
**Documented rules:** BIC must be **8 or 11 chars**; positions 1–4 = bank code (alphabetic A–Z), positions 5–6 = country code (alphabetic, ISO 3166-1 alpha-2); optional 3-char location; sets `VALID='0'B` and returns early on failure. Uses `RANK()` for A–Z range checks. Called by `PROCESS_SWIFT_PAYMENT`.

```java
// Proposed (not in context): BicValidator.java
public boolean isValid(String bic) {
    String t = bic == null ? "" : bic.trim();
    if (t.length() != 8 && t.length() != 11) return false;      // length rule
    for (int i = 0; i < 4; i++) if (!isAZ(t.charAt(i))) return false; // bank code 1-4
    for (int i = 4; i < 6; i++) if (!isAZ(t.charAt(i))) return false; // country 5-6
    return true;
}
private boolean isAZ(char c) { return c >= 'A' && c <= 'Z'; }
```

**Test cases:**
| ID | Input | Expected |
|----|-------|----------|
| BIC-1 | `"DEUTDEFF"` (8) | valid |
| BIC-2 | `"DEUTDEFF500"` (11) | valid |
| BIC-3 | `"DEUT"` (len 4 <8) | invalid (length) |
| BIC-4 | `"DE1TDEFF"` (digit in bank code) | invalid (pos 1-4 non-alpha) |
| BIC-5 | `"DEUT12FF"` (digits in country) | invalid (pos 5-6 non-alpha) |
| BIC-6 | 9-char string | invalid (not 8/11) |
| BIC-7 | leading/trailing spaces around valid 8 | valid (TRIM applied) |
| BIC-8 | null | invalid |

### Feature 11 — Transfer Request Validation (`VALIDATE_TRANSFER_REQUEST`)
**Documented rules (waterfall):** amount `> 0` and `<= 10,000,000.00`; large transfers (>10M — note: contradiction in source, see below) require **High/Urgent** priority; transfer type `SELECT` on Internal/External/Wire; Internal requires `SAME_CUSTOMER_OWNERSHIP()`; External requires `VERIFY_EXTERNAL_BANK()`; Wire requires `CHECK_WIRE_AUTHORIZATION()`; source and target accounts (`FROM_ACCT_NUM`+`FROM_SORT_CODE` vs `TO_*`) must differ; scheduled date `>= CURRENT_DT` and `DAYS_FUTURE <= 365`. Sets `ERROR_DESCRIPTION`/`LAST_ERROR_CODE`, returns early.

**Spec contradiction to flag:** one summary says amount cap is `<= 10,000,000.00`, while another says large transfers `> 10M` require H/U priority. These can't both hold at the cap boundary as literally worded — the intended rule is almost certainly: amount `> 0`, amounts `> 10M` are either rejected *or* require H/U priority. **This needs product confirmation before porting.**

```java
// Proposed (not in context): TransferValidator.java
public ValidationResult validate(TransferRequest r, LocalDate today) {
    if (r.getAmount().compareTo(BigDecimal.ZERO) <= 0) return fail("Amount must be positive");
    if (r.getAmount().compareTo(new BigDecimal("10000000.00")) > 0
            && !isHighOrUrgent(r.getPriority())) return fail("Large transfer requires H/U priority");
    switch (r.getTransferType()) {
        case INTERNAL: if (!sameOwnership(r)) return fail("Ownership mismatch"); break;
        case EXTERNAL: if (!verifyExternalBank(r)) return fail("Bank verification failed"); break;
        case WIRE:     if (!checkWireAuth(r)) return fail("Wire auth required"); break;
    }
    if (sameAccount(r)) return fail("Source and target must differ");
    if (r.getScheduledDate().isBefore(today)) return fail("Scheduled date in past");
    if (DAYS.between(today, r.getScheduledDate()) > 365) return fail("Beyond 365-day window");
    return ok();
}
```

**Test cases:**
| ID | Scenario | Expected |
|----|----------|----------|
| XFER-1 | amount = 0 | fail (positive) |
| XFER-2 | amount = -50 | fail (positive) |
| XFER-3 | amount = 100, valid internal, distinct accts, today+10 | pass |
| XFER-4 | amount = 15,000,000, priority Normal | fail (H/U required) |
| XFER-5 | amount = 15,000,000, priority Urgent, valid | pass |
| XFER-6 | Internal, ownership mismatch | fail |
| XFER-7 | External, bank verify fails | fail |
| XFER-8 | Wire, no wire auth | fail |
| XFER-9 | from == to (acct+sort) | fail |
| XFER-10 | scheduled date = yesterday | fail (past) |
| XFER-11 | scheduled date = today+366 | fail (>365) |
| XFER-12 | scheduled date = today+365 | pass (boundary) |

### Feature 5 — Customer Data Validation (`VALIDATE_CUSTOMER_DATA`)
**Documented rules:** first/last name required & alphabetic (`VERIFY` against `ALPHA_CHARS CHAR(52)`); email must contain `@` before `.` (`INDEX`); phone numeric-only after stripping `()-. ` via `TRANSLATE` (`VERIFY` against `NUMERIC_CHARS CHAR(10)`); phone field `CHAR(14)`, phone must be 10–14 digits; DOB `YYYYMMDD`; sets `VALIDATION_FAILED='1'B`, early return; routes to `SEND_ERROR_MAP` on failure.

```java
// Proposed (not in context): CustomerValidator.java
public ValidationResult validate(CustomerMaster c) {
    if (isBlank(c.getFirstName()) || !isAlpha(c.getFirstName())) return fail("First name invalid");
    if (isBlank(c.getLastName())  || !isAlpha(c.getLastName()))  return fail("Last name invalid");
    if (c.getEmail().indexOf('@') < 0
            || c.getEmail().indexOf('@') > c.getEmail().indexOf('.')) return fail("Email invalid");
    String digits = c.getPhone().replaceAll("[()\\-. ]", "");    // TRANSLATE strip
    if (!digits.matches("\\d{10,14}")) return fail("Phone invalid");
    return ok();
}
```

**Test cases:**
| ID | Field | Input | Expected |
|----|-------|-------|----------|
| CUST-1 | firstName | `""` | fail (required) |
| CUST-2 | firstName | `"John3"` | fail (non-alpha) |
| CUST-3 | lastName | `"O'Brien"` | fail (apostrophe non-alpha per ALPHA_CHARS) |
| CUST-4 | email | `"abc.def@x"` (@ after .) | fail |
| CUST-5 | email | `"a@b.com"` | pass |
| CUST-6 | phone | `"(555) 123-4567"` → 10 digits | pass |
| CUST-7 | phone | `"12345"` (5 digits) | fail (<10) |
| CUST-8 | phone | `"1234567890abcd"` | fail (non-numeric) |
| CUST-9 | all valid | valid record | pass |

### Feature 10 — Overdraft Enforcement (`CHECK_OVERDRAFT`)
**Documented rules:** `NEW_BALANCE = AVAILABLE_BALANCE - DEBIT_AMOUNT`; `OVERDRAFT_AMT = ABS(NEW_BALANCE)`; `IF Balance < -Overdraft_Limit THEN SIGNAL OVERDRAFT` (reject or assess fee).

**Test cases:**
| ID | Balance | Debit | Limit | Expected |
|----|---------|-------|-------|----------|
| OD-1 | 100 | 50 | 200 | ok (new=50) |
| OD-2 | 100 | 250 | 200 | ok within limit (new=-150, -150 > -200) |
| OD-3 | 100 | 350 | 200 | SIGNAL OVERDRAFT (new=-250 < -200) |
| OD-4 | 100 | 300 | 200 | boundary (new=-200, not < -200) → ok/fee per policy |

### Feature 16 — Mortgage Payment (`CALCULATE_MORTGAGE_PAYMENT`)
**Documented formula:** `PMT = P * [r(1+r)^n] / [(1+r)^n - 1]`, `r = ANNUAL_RATE/12`; must handle zero-denominator edge case.

**Test cases:**
| ID | P | rate | n | Expected |
|----|---|------|---|----------|
| MTG-1 | 100000 | 0.06 | 360 | ~599.55 (verify) |
| MTG-2 | 100000 | 0.0 | 360 | zero-rate branch (P/n = 277.78) — must not divide by zero |
| MTG-3 | any | any | 0 | guarded (no div-by-zero) |

### Feature 17 — Interest Accrual (`CALCULATE_INTEREST_ACCRUAL`)
**Documented formula:** `DAILY_RATE = ANNUAL_RATE/365.0`; `ACCRUED = BALANCE * DAILY_RATE * DAYS_ELAPSED`.

**Test cases:** ACR-1 (balance=10000, rate=0.05, days=30 → verify ~41.10); ACR-2 (days=0 → 0); ACR-3 (negative balance → negative accrual).

### Feature 21–23 — Credit Scoring / Quorum / Risk Decision
**Documented rules:** query 5 bureaus, wait for **3/5 quorum** before computing composite; `SCORE = Σ(FACTOR_WEIGHT(i) * FACTOR_VALUE(i))`; DFT trend `Real = Σ Score*cos(2πi/12)`; decision thresholds: **≥720 auto-approve**, **650–719 conditional review**, **<650 auto-deny**; `MIN_CREDIT_SCORE = 600` referenced separately (note: 600 vs 650 thresholds appear in two places — confirm).

**Test cases:**
| ID | Scenario | Expected |
|----|----------|----------|
| CRED-1 | only 2/5 bureaus respond | no composite computed / wait/timeout |
| CRED-2 | 3/5 respond | composite computed |
| CRED-3 | composite = 720 | auto-approve |
| CRED-4 | composite = 700 | conditional review |
| CRED-5 | composite = 649 | auto-deny |
| CRED-6 | composite = 719 | conditional review (boundary) |

### Feature 34 — Error Handling Suite (ON-condition handlers)
**Documented behavior:** `CONVERSION` → log + sanitize (`ONSOURCE='0'`, continue); `STRINGSIZE` → warn + continue (non-fatal, business tolerance); `SIZE/FIXEDOVERFLOW/OVERFLOW/ZERODIVIDE/SUBSCRIPTRANGE/RECORD/STORAGE/AREA` → `SIGNAL ERROR` → centralized handler; `ATTENTION` → `ERRHAND_CLEANUP(999)` graceful shutdown; centralized cleanup does `ROLLBACK WORK`, `FREE`, `CLOSE`.

**Test cases:** ERR-1 (bad numeric convert → sanitized to 0, processing continues); ERR-2 (string truncation → warn logged, no abort); ERR-3 (divide by zero → transaction rolled back); ERR-4 (subscript out of range → error path); ERR-5 (attention/interrupt → graceful cleanup + rollback).

---

## Front-End Features (consolidated)

Grouped by screen, each supporting the mapped back-end features:

1. **Main Menu / Navigation** (F32) — AID-key equivalent routing to modules.
2. **Customer screens** (F1–F6) — search+pagination, create/edit forms with inline validation mirroring `VALIDATE_CUSTOMER_DATA`, delete-with-open-account-check dialog, manual-review queue.
3. **Account screens** (F7–F10) — lifecycle management, status display, holder add/remove, overdraft warnings.
4. **Transfer screen** (F11–F15) — form with live rule messaging (amount cap, priority for large, type-specific auth prompts), fee preview, confirmation, optional routing path view.
5. **Financial tools** (F16–F18) — amortization schedule table, accrued-interest display, statement cycle config.
6. **Credit decision screen** (F21–F23) — bureau response status, composite score + trend chart, approve/review/deny outcome.
7. **SWIFT/FX** (F28–F31) — MT103/MT202 payment form with BIC field validation, currency selector + FX rate display, i18n/DBCS-correct rendering, picture-formatted amounts.
8. **Audit & reports** (F24–F26) — read-only audit log viewer (no edit/delete — enforces immutability rule), report download/print view with pagination.
9. **Admin** (F20, F35, F36) — batch generation params, config rule engine screen, KYC form.
10. **Cross-cutting** (F33, F34) — global error toast/screen, session/security handling.

---

## Testing Plan

- **Unit tests** — one per validator/calculator (`BicValidatorTest`, `TransferValidatorTest`, `CustomerValidatorTest`, `MortgageCalculatorTest`, `InterestServiceTest`, `OverdraftServiceTest`), using the case tables above.
- **Integration tests** — CRUD flows against test DB; transactional fund-movement rollback; async credit scoring quorum; append-only audit (attempt update/delete must be rejected).
- **Boundary tests** — all the `<= / < / >` boundaries called out (10M cap, 365-day window, 8/11 BIC length, 720/650/600 credit thresholds, overdraft limit equality).
- **Contract tests** — REST endpoints replacing CICS transactions.
- **Manual/E2E** — each front-end screen against its mapped feature.

**Proposed test file locations (not in context):** `src/test/java/com/bank/customer/CustomerValidatorTest.java`, `.../transfer/TransferValidatorTest.java`, `.../swift/BicValidatorTest.java`, etc.

---

## Risks & Considerations

- **Precision:** must use `BigDecimal`, not `double`, for all money (mirrors PL/I `FIXED DEC(15,2)`).
- **VSAM/QSAM keyed access + offset chains** (`TRAVERSE_TRANSACTION_CHAIN`, archive O(1) hashing) don't map 1:1 to JPA — need indexed-table redesign.
- **Async quorum semantics** and **complex-number DFT trend** must be verified numerically against legacy output.
- **DBCS/Unicode & picture-drift** formatting fidelity is subtle — needs golden-file comparison tests.
- **Immutability** of audit trail must be enforced at DAO + DB (no UPDATE/DELETE grants).
- **COBOL interop** (`BNKMAIN` COBOL legacy interoperability) — if any COBOL modules remain, bridging strategy is undefined in context.

---

## Critical Files for Implementation (from context)

- `Bank-PLI/BNKMAIN.PLI` — dispatcher + `BINDALL`; source of routing logic.
- `Bank-PLI/BNKCUST.PLI` — customer CRUD + `VALIDATE_CUSTOMER_DATA` + `VALIDATE_BIC`.
- `Bank-PLI/BNKFX.PLI` — SWIFT/BIC/FX + picture/DBCS demos.
- `Bank-PLI/jcl/COMPILE.JCL` — build pipeline reference for CI/CD replacement.
- (Referenced but not provided as files) `BNKACCT`, `BNKXFER`, `BNKCRED`, `BNKBATC`, `BNKAUDIT` — needed to port Features 7–26.

---

## Unknown / not evidenced

- Actual PL/I source line bodies, exact Java package/file paths, and target framework choices are **not in the context** (all Java code and paths above are Proposed).
- **Spec contradictions requiring product confirmation:** (a) transfer amount cap `<= 10M` vs "large transfers >10M require H/U priority"; (b) credit threshold `MIN_CREDIT_SCORE = 600` vs decision thresholds `<650 deny / 650–719 review / ≥720 approve`.
- `BNKACCT/BNKXFER/BNKCRED/BNKBATC/BNKAUDIT` are only summarized via procedure names — their source files are not in context, so their ports depend on retrieving those sources.