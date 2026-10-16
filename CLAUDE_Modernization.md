# Modernization source inventory

This inventory is the modernization baseline for this Bank PLI checkout, which
contains the Java backend and the React frontend. Counts come from parsed source
trees, using Tree-sitter.

## Current baseline

The inventory script lives in the main Bank PLI repository, not in this
checkout. Run it from that repository and pass this checkout's path:

```bash
python3 -m pip install -r scripts/requirements-tree-sitter.txt
python3 scripts/service_metrics.py /Users/adaptsadmin/workspace/BankPLI --exclude-tests
```

| Service | Source files | Types | Functions | Business rules | Data elements | DB modules |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `bank-pli-backend` | 50 | 62 | 158 | 67 | 75 | 3 |
| `bank-pli-frontend` | 17 | 1 | 116 | 21 | 132 | 0 |
| **Total** | **67** | **63** | **274** | **88** | **207** | **3** |

The baseline excludes test sources. It includes 49 Java and 18 JavaScript/JSX
source files.

## Metric definitions

- **Types** — Java classes, interfaces, enums, and records, and JavaScript
  classes.
- **Functions** — Java methods and constructors, and JavaScript function
  declarations, expressions, arrow functions, and methods.
- **Business rules** — syntactic decision points: `if`, `switch`, and ternary
  expressions. This is an objective complexity proxy, not a claim that every
  decision point is a domain rule.
- **Data elements** — structural fields and properties: Java fields and
  JavaScript object properties. Local variables are deliberately excluded.
- **DB modules** — Java classes carrying the JPA `@Entity` annotation. All 3
  are in the backend.

## Runtime database assets

`bank-pli-backend/data` contains one H2 database file, `javabank.mv.db`. It is
runtime data, not a source-level database module, so it is reported separately
from the 3 JPA entity modules above.

## Dependencies

| Service | Ecosystem | Direct | Direct dev/test | Resolved (incl. transitive) |
| --- | --- | ---: | ---: | ---: |
| `bank-pli-backend` | Maven | 7 | 1 | 99 |
| `bank-pli-frontend` | npm | 8 | 4 | 123 |
| **Total** | | **15** | **5** | **222** |

The two services declare 15 direct dependencies and resolve to **222 unique
packages**. They share no packages, so the unique total equals the sum.

- **Direct** counts entries in `pom.xml` or `package.json`.
- **Resolved** counts Maven's resolved artifact list (all scopes) and every
  installed package in `package-lock.json`.

## Infrastructure

This checkout has no container infrastructure: there is no Compose file and no
Dockerfile. The only infrastructure files are 2 CI workflows, one for the
backend and one for the frontend.

## Re-running the inventory

`scripts/service_metrics.py` scans each service directory and ignores generated
directories such as `target`, `dist`, and `node_modules`. Use JSON when the
results need to feed another report:

```bash
python3 scripts/service_metrics.py /Users/adaptsadmin/workspace/BankPLI --exclude-tests --format json
```

The backend's resolved Maven count comes from
`bank-pli-backend/target/dependency-list.txt`. Regenerate it from
`bank-pli-backend` with:

```bash
mvn -q -DskipTests dependency:list -DoutputFile=target/dependency-list.txt
```
