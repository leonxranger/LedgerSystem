# LedgerSystem

A transactional double-entry ledger built with Java and Spring Boot, designed around financial correctness, concurrency safety, and an auditable source of truth.

LedgerSystem aims to demonstrate the core engineering principles behind financial transaction systems: balanced ledger entries, atomic database transactions, safe concurrent account updates, idempotent request handling, and reliable balance reporting.

> **Project status:** Update this section to reflect the current development stage. Document which features are implemented, tested, and still under development.

## Table of Contents

* [Overview](#overview)
* [Key Features](#key-features)
* [Technology Stack](#technology-stack)
* [Architecture](#architecture)
* [Prerequisites](#prerequisites)
* [Getting Started](#getting-started)
* [Configuration](#configuration)
* [Running the Application](#running-the-application)
* [API Documentation](#api-documentation)
* [Testing](#testing)
* [Stopping and Cleaning Up](#stopping-and-cleaning-up)
* [Project Structure](#project-structure)
* [Design Considerations](#design-considerations)
* [Troubleshooting](#troubleshooting)

## Overview

LedgerSystem models financial transactions using double-entry bookkeeping. Each transaction consists of ledger entries whose debits and credits must balance.

PostgreSQL serves as the authoritative store for financial records. Redis supports selected performance optimizations, including caching and idempotency tracking.

The primary engineering goals are:

* Preserve financial invariants across every transaction.
* Prevent race conditions during concurrent account updates.
* Handle retried requests without accidentally duplicating financial operations.
* Derive account balances from ledger records.
* Detect and correct cache inconsistencies without modifying authoritative ledger history.

## Key Features

* **Double-entry bookkeeping:** Validates that every transaction balances before it is committed.
* **Precise monetary arithmetic:** Uses Java `BigDecimal` and appropriate PostgreSQL numeric types instead of floating-point arithmetic.
* **Transactional integrity:** Uses database transactions to ensure that related ledger changes succeed or fail together.
* **Concurrency control:** Uses database locking and transaction isolation to protect concurrent updates to shared accounts.
* **Idempotent request handling:** Uses idempotency keys to identify retries of previously submitted operations.
* **Balance reporting:** Calculates account balances from ledger entries rather than treating cached balances as authoritative.
* **Cache reconciliation:** Compares cached values against database-derived balances and repairs stale cache entries where appropriate.
* **Versioned database schema:** Uses Flyway migrations to manage database changes.
* **Integration testing:** Uses Testcontainers to test against real PostgreSQL and other required services in disposable containers.

Only retain features above that are actually implemented and verified in the current codebase.

## Technology Stack

| Component           | Technology                  | Responsibility                                              |
| ------------------- | --------------------------- | ----------------------------------------------------------- |
| Language            | Java 21                     | Application logic and monetary calculations                 |
| Backend             | Spring Boot                 | REST APIs, dependency injection, and transaction management |
| Persistence         | Spring Data JPA / Hibernate | Object-relational mapping                                   |
| Database            | PostgreSQL                  | Authoritative financial records                             |
| Migrations          | Flyway                      | Version-controlled database schema                          |
| Cache               | Redis                       | Selected cached data and idempotency records                |
| Integration testing | JUnit and Testcontainers    | Automated tests against real infrastructure                 |
| Containerization    | Docker                      | Local infrastructure and test dependencies                  |

## Architecture

The request lifecycle is intended to follow this general flow:

1. A client submits a transaction request with an idempotency key.
2. The application validates the request and checks whether the key represents an existing operation.
3. The service validates the double-entry invariant and the relevant business rules.
4. The database transaction locks the necessary records and applies the ledger changes atomically.
5. PostgreSQL remains the source of truth for committed financial data.
6. Subsequent balance reads use the reporting layer, with Redis used only where appropriate.
7. Reconciliation jobs compare cached data against authoritative database calculations.

The precise ordering of idempotency reservation, transaction execution, and result persistence depends on the implementation. These steps must be coordinated so that concurrent retries cannot execute the same operation twice.

<img width="2752" height="1536" alt="WORKFLOW DIAGRAM" src="https://github.com/user-attachments/assets/8f21d0e5-ba81-4d24-9e62-6a86b0ccfd3e" />


## Prerequisites

Install the following before running the project:

* Java 21 JDK
* Git
* Docker Engine and Docker Compose, if using containerized infrastructure
* Maven or the project's configured build tool
* Access to PostgreSQL and Redis, either through Docker or existing local installations

Verify the installations:

```
java -version
git --version
docker --version
docker compose version
```

If the repository includes the Maven Wrapper, use `./mvnw` instead of relying on a globally installed Maven version.

## Getting Started

### 1. Clone the repository

```
git clone <YOUR_REPOSITORY_URL>
cd LedgerSystem
```

Replace `<YOUR_REPOSITORY_URL>` with the repository's actual Git URL.

### 2. Configure the environment

Create a local environment configuration using the variables documented in the Configuration section.

Do not commit passwords, access tokens, or other secrets. Keep local environment files out of version control.

### 3. Start the infrastructure

If the repository provides a `compose.yaml` or `docker-compose.yml` for PostgreSQL and Redis, start those services:

```
docker compose up -d
```

Check their status:

```
docker compose ps
```

Confirm that PostgreSQL and Redis are healthy before starting the application.

If Compose is not configured in the repository, start these dependencies separately using the documented local configuration.

### 4. Apply database migrations

Flyway migrations should run automatically during application startup if configured that way.

Otherwise, use the project's documented Flyway workflow. Do not manually create tables that are already managed by migrations.

### 5. Start the application

With the Maven Wrapper:

```
./mvnw spring-boot:run
```

Alternatively, build and run the packaged application:

```
./mvnw clean package
java -jar target/<APPLICATION_JAR>.jar
```

Replace the JAR placeholder with the actual artifact generated by the build.

Check the startup logs for the application port, successful database connection, and migration status.

## Configuration

Document every required environment variable in this table. The names below are examples; align them with the actual application configuration.

| Variable                     | Purpose                        | Example                                   |
| ---------------------------- | ------------------------------ | ----------------------------------------- |
| `SPRING_DATASOURCE_URL`      | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/ledger` |
| `SPRING_DATASOURCE_USERNAME` | Database username              | `ledger_user`                             |
| `SPRING_DATASOURCE_PASSWORD` | Database password              | Set locally; never commit                 |
| `SPRING_DATA_REDIS_HOST`     | Redis hostname                 | `localhost`                               |
| `SPRING_DATA_REDIS_PORT`     | Redis port                     | `6379`                                    |
| `SERVER_PORT`                | Application HTTP port          | `8080`                                    |

For each variable, specify whether it is required, its default value, and whether it is safe to use in development.

Do not assume Spring Boot automatically reads a `.env` file. Use the configuration mechanism actually supported by the project.

## Running the Application

Once the application and dependencies are running:

* Inspect the application startup logs.
* Send a request to a documented API endpoint.
* Check the database to verify that the expected ledger records were committed.
* Retry a transaction with the same idempotency key to verify duplicate-request handling.
* Run concurrent requests against shared accounts to test the locking and transaction guarantees.

Use only the endpoints and request formats defined by the current API implementation.

## API Documentation

LedgerSystem exposes REST endpoints for account management, transaction processing, and financial reporting. All request and response bodies use JSON unless otherwise specified.



**Base URL:**  https://ledgersystem-utiq.onrender.com

### 1. Account Provisioning & Management

#### Create an account

`POST /accounts`

Creates a new account with a backend-generated UUID business code.

**Request Headers**

```
Content-Type: application/json
```

**Request Body**

```
{
  "name": "Cash Account",
  "type": "ASSET"
}
```

| Field      | Type       | Required   | Description                                                    |
| ---------- | ---------- | ---------- | -------------------------------------------------------------- |
| `name`     | String     | Yes        | Name of the account                                            |
| `type`     | Enum       | Yes        | One of `ASSET`, `LIABILITY`, `EQUITY`, `REVENUE`, or `EXPENSE` |

The `code` field is generated by the backend and does not need to be supplied by the client.

**Success Response —** `200 OK`

Returns the newly created account as JSON, including its generated business code.

```
{
  "code": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Cash Account",
  "type": "ASSET"
}
```

*The response is illustrative; the exact JSON fields depend on the serialized* `Accounts`* entity.*

**Possible Errors**

* `400 Bad Request` — Invalid account type or malformed request.

#### Retrieve all accounts

`GET /accounts`

Retrieves a paginated list of accounts.

**Query Parameters**

| Parameter    | Type       | Default    | Description                         |
| ------------ | ---------- | ---------- | ----------------------------------- |
| `pageNumber` | Integer    | `0`        | Zero-based page index               |
| `pageSize`   | Integer    | `50`       | Maximum number of accounts per page |

**Example Request**

```
GET /accounts?pageNumber=0&pageSize=10
```

**Success Response —** `200 OK`

Returns a Spring Data `Page<Accounts>` JSON representation containing the account records and pagination metadata.

```
{
  "content": [
    {
      "code": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Cash Account",
      "type": "ASSET"
    }
  ],
  "pageable": {},
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

*The example is abbreviated. Actual pagination metadata depends on the Spring Boot and Spring Data configuration.*

#### Retrieve an account by code

`GET /accounts/{code}`

Retrieves a specific account using its UUID business code.

**Path Parameters**

| Parameter  | Type       | Description                               |
| ---------- | ---------- | ----------------------------------------- |
| `code`     | String     | Unique business identifier of the account |

**Example Request**

```
GET /accounts/550e8400-e29b-41d4-a716-446655440000
```

**Success Response —** `200 OK`

Returns the matching account as JSON.

```
{
  "code": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Cash Account",
  "type": "ASSET"
}
```

**Possible Errors**

* Account not found — The current implementation throws a `RuntimeException` with an account-not-found message. The resulting HTTP status depends on the application's exception handling.

#### Retrieve an account balance

`GET /accounts/{accountCode}/balance`

Calculates and retrieves the balance associated with a specific account.

**Path Parameters**

| Parameter     | Type       | Description                       |
| ------------- | ---------- | --------------------------------- |
| `accountCode` | String     | UUID business code of the account |

**Example Request**

```
GET /accounts/550e8400-e29b-41d4-a716-446655440000/balance
```

**Success Response —** `200 OK`

Returns the computed balance as a JSON numeric value.

```
12500.75
```

The returned value is illustrative. The actual balance depends on the account's recorded ledger entries and the application's balance calculation rules.

***

### 2. Transaction Processing

#### Create a transaction

`POST /transactions`

Records a financial transaction and its associated ledger lines. Every transaction must satisfy the double-entry zero-sum invariant: the sum of all ledger line amounts must equal exactly zero.

**Request Headers**

```
Content-Type: application/json
Idempotency-Key: <UNIQUE_REQUEST_KEY>
```

The `Idempotency-Key` identifies a request and is used to detect duplicate submissions.

**Request Body**

```
{
  "description": "Transfer between accounts",
  "itemlist": [
    {
      "accountCode": "550e8400-e29b-41d4-a716-446655440000",
      "amount": -1000.00
    },
    {
      "accountCode": "660e8400-e29b-41d4-a716-446655440001",
      "amount": 1000.00
    }
  ]
}
```

| Field                    | Type       | Required   | Description                                  |
| ------------------------ | ---------- | ---------- | -------------------------------------------- |
| `description`            | String     | Yes        | Description of the transaction               |
| `itemlist`               | Array      | Yes        | Ledger lines associated with the transaction |
| `itemlist[].accountCode` | String     | Yes        | Business code of an existing account         |
| `itemlist[].amount`      | BigDecimal | Yes        | Signed monetary amount for the ledger line   |

**Validation Rules**

* Every referenced account must exist.
* The sum of all ledger line amounts must equal exactly zero.
* Monetary calculations must preserve decimal precision.
* A unique idempotency key must be supplied for each new logical operation.

**Success Response —** `200 OK`

Returns the saved transaction as a `TransactionResponse` DTO, including transaction details and associated ledger lines without circular entity references.

```
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "description": "Transfer between accounts",
  "itemlist": [
    {
      "accountCode": "550e8400-e29b-41d4-a716-446655440000",
      "amount": -1000.00
    },
    {
      "accountCode": "660e8400-e29b-41d4-a716-446655440001",
      "amount": 1000.00
    }
  ]
}
```

*The response fields are illustrative and should be aligned with the actual* `TransactionResponse`* DTO.*

**Possible Errors**

* `409 Conflict` — The idempotency key already exists according to the current Redis-backed duplicate-request check.
* Unbalanced transaction — The zero-sum validation fails, and the database transaction is expected to roll back.
* Invalid account or malformed request — The response depends on the application's validation and exception-handling configuration.

**Important:** A rejected or failed transaction must not leave partially committed ledger entries.

#### Retrieve a transaction

`GET /transactions/{id}`

Retrieves a previously recorded transaction using its transaction ID.

**Path Parameters**

| Parameter  | Type       | Description                          |
| ---------- | ---------- | ------------------------------------ |
| `id`       | UUID       | Unique identifier of the transaction |

**Example Request**

```
GET /transactions/770e8400-e29b-41d4-a716-446655440002
```

**Success Response —** `200 OK`

Returns a `TransactionResponse` DTO containing the transaction details and associated ledger lines.

**Possible Errors**

* Transaction not found — The current implementation throws a `RuntimeException` with the message `Transaction not found`. The resulting HTTP status depends on the application's exception handling.

***

### 3. Financial Reporting

#### Generate a balance sheet

`GET /reports/balance-sheet`

Calculates aggregated account balances from the beginning of the ledger history up to a specified date and time.

**Query Parameters**

| Parameter  | Type          | Required   | Description                                                  |
| ---------- | ------------- | ---------- | ------------------------------------------------------------ |
| `date`     | LocalDateTime | Yes        | End date and time for the report, formatted as ISO Date-Time |

**Example Request**

```
GET /reports/balance-sheet?date=2026-10-10T23:59:59
```

**Success Response —** `200 OK`

Returns a JSON array of `AccountBalanceProjection` objects.

```
[
  {
    "account_Code": "550e8400-e29b-41d4-a716-446655440000",
    "account_Name": "Cash Account",
    "balance": 12500.75
  },
  {
    "account_Code": "660e8400-e29b-41d4-a716-446655440001",
    "account_Name": "Revenue Account",
    "balance": 12500.75
  }
]
```

The example values are illustrative. The returned balances depend on the ledger data and the application's account-balance conventions.

#### Generate an income statement

`GET /reports/income-statement`

Calculates aggregated account balances for transactions recorded within a specified date range.

**Query Parameters**

| Parameter   | Type          | Required   | Description                                            |
| ----------- | ------------- | ---------- | ------------------------------------------------------ |
| `startDate` | LocalDateTime | Yes        | Start of the reporting period, in ISO Date-Time format |
| `endDate`   | LocalDateTime | Yes        | End of the reporting period, in ISO Date-Time format   |

**Example Request**

```
GET /reports/income-statement?startDate=2026-10-01T00:00:00&endDate=2026-10-31T23:59:59
```

**Success Response —** `200 OK`

Returns a JSON array of `AccountBalanceProjection` objects representing calculated balances for the requested period.

```
[
  {
    "account_Code": "660e8400-e29b-41d4-a716-446655440001",
    "account_Name": "Revenue Account",
    "balance": 25000.00
  },
  {
    "account_Code": "880e8400-e29b-41d4-a716-446655440003",
    "account_Name": "Expense Account",
    "balance": 12500.00
  }
]
```

The response is illustrative. The exact results depend on the underlying ledger entries, date filtering, and account-balance sign conventions.

***

### API Notes

* **Content type:** Use `application/json` for JSON request bodies.
* **Identifiers:** Account business codes are generated UUIDs. Transaction IDs are UUIDs.
* **Monetary precision:** Amounts are represented using decimal values; clients should avoid binary floating-point arithmetic when performing financial calculations.
* **Date-time format:** Reporting endpoints expect ISO Date-Time values, such as `2026-10-10T23:59:59`. Confirm the application's timezone and boundary-inclusion rules before relying on period-end reports.
* **Authentication:** Authentication requirements are not specified in the available endpoint definitions. Verify the application's security configuration before exposing these endpoints.
* **Error handling:** HTTP statuses for exceptions not explicitly mapped by the application may differ from the expected outcomes described above.

Add a complete endpoint summary tableStandardize success and error responses

## Testing

Run the test suite:

```
./mvnw test
```

Run a clean build:

```
./mvnw clean verify
```

If the integration tests use Testcontainers, ensure Docker is running before executing them.

Recommended test categories include:

* Unit tests for transaction validation and balance calculations.
* Integration tests for database persistence and Flyway migrations.
* Concurrency tests for simultaneous updates to the same accounts.
* Idempotency tests for repeated and concurrent requests.
* Reconciliation tests for stale or inconsistent cache values.
* Failure tests verifying that partial ledger writes are rolled back.

Document any separate commands required to run specific test classes or test profiles.

## Stopping and Cleaning Up

### Stop the application

If the application is running in the foreground, press:

```
Ctrl+C
```

### Stop infrastructure containers

Stop the Compose services without deleting their persistent data:

```
docker compose stop
```

Alternatively, stop and remove the Compose-managed containers and network:

```
docker compose down
```

### Remove disposable development data

Only when you intentionally want to reset the local environment:

```
docker compose down -v
```

**Warning:** The `-v` option removes Compose-managed named and anonymous volumes. This can permanently delete your local PostgreSQL data. Never use it against an environment containing data you need to preserve.

Do not use broad Docker cleanup commands on a shared development machine without checking what they will remove.

## Project Structure

Document the actual package structure. A possible organization is:

```
LedgerSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── .../
│   │   │       ├── controller/    # REST endpoints
│   │   │       ├── service/       # Transaction and business logic
│   │   │       ├── repository/    # Database access
│   │   │       ├── entity/        # Persistent domain entities
│   │   │       ├── dto/           # API request and response models
│   │   │       └── config/        # Application configuration
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/      # Flyway migrations
│   └── test/
├── compose.yaml                   # If provided
├── pom.xml
└── README.md
```

This is an illustrative layout, not a claim about the repository's current directories. Update it to match the actual codebase.

## Design Considerations

### Financial correctness

Ledger entries should be immutable once committed, and each transaction should preserve the accounting invariant. Corrections should use explicit compensating entries rather than silently rewriting historical records.

### Concurrency

Database locks and transaction isolation must be selected according to the operations being protected. Validate behavior with concurrent integration tests rather than assuming that a particular isolation level alone eliminates every race condition.

### Idempotency

A duplicate request should not create another financial operation. The implementation must account for simultaneous requests using the same key, transaction failures, and retries after an uncertain response.

### Caching

Redis is an optimization, not the financial source of truth. Cache invalidation and reconciliation must account for transaction commits, stale reads, and concurrent updates.

### Performance

Measure transaction throughput, latency, database contention, and batch behavior under representative workloads. Document benchmark methodology and results only when they have been measured.

## Troubleshooting

| Problem                                  | What to check                                                             |
| ---------------------------------------- | ------------------------------------------------------------------------- |
| Application cannot connect to PostgreSQL | JDBC URL, credentials, port mapping, container health                     |
| Redis connection failure                 | Redis availability and configured hostname/port                           |
| Docker-backed tests fail                 | Docker daemon status and Testcontainers configuration                     |
| Flyway migration failure                 | Migration ordering, schema history, and database state                    |
| Port already in use                      | Existing process using the configured application or database port        |
| Duplicate transaction response           | Idempotency key reuse and the API's documented duplicate-request behavior |

## Contributing

For development contributions:

1. Create a feature branch.
2. Make the change and add appropriate tests.
3. Run the test suite.
4. Open a pull request describing the change, its rationale, and its test coverage.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
