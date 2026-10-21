<<<<<<< HEAD
# Java-Projects
=======
# Beginner-friendly Accounting System

A simple full-stack accounting starter project with a Spring Boot API, PostgreSQL database, and React interface.

## What it includes

- Login with JWT authentication and a seeded demo administrator
- Dashboard showing sales, purchases, receipts, and payments
- Add and delete forms for purchases, sales, receipts, and payments
- View and print receipts after recording or from the saved receipts list
- Add and delete journal entries
- Simple income and cash activity reports
- PostgreSQL setup using Docker Compose

## Requirements

- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker Desktop (or a local PostgreSQL 16 server)

## Run it on Windows

Open a terminal in this project folder.

1. Start PostgreSQL:

   ```powershell
   docker compose up -d postgres
   ```

   This starts database `accounting_db` with username `accounting` and password `accounting` on port `5432`.

2. Start the backend in a terminal:

   ```powershell
   cd backend
   mvn spring-boot:run
   ```

   The API starts at `http://localhost:8080`. Spring creates the tables automatically when it starts.

3. Start the frontend in another terminal:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

   Open the Vite URL shown in the terminal (normally `http://localhost:5173`).

4. Sign in with:

   ```text
   Username: admin
   Password: admin123
   ```

The default credentials and JWT key are for local development only. Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and a long random `JWT_SECRET` environment variable before using a non-local environment. Do not expose the demo database or commit production credentials.

## Stop the database

```powershell
docker compose down
```

Data stays in the Docker volume. To remove that local data, explicitly run `docker compose down -v`.

## Project layout

```text
backend/     Spring Boot API, security, services, and entities
frontend/    React pages and shared UI components
database/    PostgreSQL schema reference
docs/        Architecture and API notes
```

See [docs/architecture.md](docs/architecture.md) and [docs/api-documentation.md](docs/api-documentation.md).
>>>>>>> 38e2ebd (Add Acc-Sys project)
