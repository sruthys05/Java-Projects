# API documentation

All routes use `http://localhost:8080`. Except for login and registration, include:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

Dates use `YYYY-MM-DD`; amounts are decimal numbers. The demo account is `admin` / `admin123`.

## Authentication

| Method | Route | Request | Result |
|---|---|---|---|
| POST | `/api/auth/login` | `{"username":"admin","password":"admin123"}` | Token and username |
| POST | `/api/auth/register` | `{"username":"alex","password":"secret1"}` | Creates a USER and returns a token |

## Accounting records

For each route below, `GET` lists entries, `GET /{id}` reads one, `POST` creates, `PUT /{id}` updates, and `DELETE /{id}` removes an entry.

| Route | Example create request |
|---|---|
| `/api/purchases` | `{"date":"2026-10-21","supplier":"Office Supply Co","description":"Paper","amount":35.50}` |
| `/api/sales` | `{"date":"2026-10-21","customer":"Taylor","description":"Consulting","amount":250.00}` |
| `/api/receipts` | `{"date":"2026-10-21","fromName":"Taylor","description":"Invoice 1001","amount":250.00}` |
| `/api/payments` | `{"date":"2026-10-21","toName":"Office Supply Co","description":"Invoice 42","amount":35.50}` |
| `/api/journals` | `{"date":"2026-10-21","description":"Opening entry","debit":100.00,"credit":100.00}` |

## Other routes

- `GET /api/dashboard` returns sales, purchases, receipts, payment totals, entry counts, and a simple net-income estimate.
- `GET /api/users` (administrator only) returns user IDs, usernames, and roles (never password hashes).

Invalid records receive HTTP 400; missing record IDs receive HTTP 404. Unauthenticated requests to protected routes receive HTTP 401.
