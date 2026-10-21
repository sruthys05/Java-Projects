# Architecture

This project is a small three-part accounting application intended for local learning and development.

```text
React (Vite) :5173
       |
       | JSON REST requests + bearer token
       v
Spring Boot :8080
       |
       | Spring Data JPA
       v
PostgreSQL :5432
```

## Backend

- `controller/` maps HTTP routes to service methods.
- `service/` contains authentication, summary, and record operations.
- `repository/` uses Spring Data JPA to read and write PostgreSQL data.
- `entity/` maps users, purchases, sales, receipts, payments, and journal entries to tables.
- `security/` handles password authentication and JWT bearer tokens.
- `exception/` formats common API errors.

The API creates tables automatically in development using `spring.jpa.hibernate.ddl-auto=update`. `database/schema.sql` is also provided as a readable PostgreSQL schema reference.

## Frontend

React pages use the small shared API helper in `src/services/api.js`. A login token is stored in browser local storage and sent to protected endpoints. The reusable transaction form/table is shared by purchases, sales, receipts, and payments.

## Learning-project limits

This is a starter application, not production accounting software. It has no tax handling, fiscal-period closing, inventory, multi-currency, audit trail, or double-entry validation. The dashboard's income estimate is sales minus purchases; cash activity is receipts minus payments. Do not use it as a substitute for reviewed financial records.
