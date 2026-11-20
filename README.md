# Park System API

[![CI](https://github.com/MateuSkywalker90/park-system/actions/workflows/ci.yml/badge.svg)](https://github.com/MateuSkywalker90/park-system/actions/workflows/ci.yml)

A REST API for running a paid parking lot: it manages customers and parking spaces, registers vehicle check-in and check-out with a receipt number, calculates the fee by time parked, applies a loyalty discount, and lets customers download their parking history as a PDF.

Built with **Java 17, Spring Boot 3, Spring Security + JWT**, and covered by **50+ integration tests** that run against an in-memory database on every push.

> This project started as the hands-on project of a Spring Boot REST API course. Later fixes — a report data leak between clients, integration tests that had silently stopped running, time zone and overflow bugs — are described in the commit history.

## Features

- **JWT authentication** with two roles: `ADMIN` operates the parking lot, `CLIENT` sees only their own data
- **Check-in** assigns the first free parking space and generates a receipt number (e.g. `20241013-101200`)
- **Check-out** frees the space and calculates the fee and discount
- **Paginated history** of parking records per customer (by CPF for admins, by logged-in user for clients)
- **PDF report** of a client's parking history (JasperReports)
- **OpenAPI / Swagger UI** documentation for every endpoint
- **Auditing**: created/updated date and user are recorded automatically on every record

## Pricing rules

| Time parked | Fee |
|---|---|
| Up to 15 minutes | R$ 5.00 |
| Up to 60 minutes | R$ 9.25 |
| Each additional 15 minutes (or part) | + R$ 1.75 |

**Loyalty discount:** every 10th visit gets **30% off**.

The rules live in [`ParkingUtils`](src/main/java/com/moliveira/demo_park_api/util/ParkingUtils.java).

## API

All endpoints are under `/api/v1`. Send the token from `/auth` as `Authorization: Bearer <token>`.

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `POST` | `/users` | public | Create a user account |
| `POST` | `/auth` | public | Log in and receive a JWT |
| `GET` | `/users` | ADMIN | List users |
| `GET` | `/users/{id}` | ADMIN, or the user themselves | Get a user |
| `PATCH` | `/users/{id}` | the user themselves | Change password |
| `POST` | `/clients` | CLIENT | Register customer details (name, CPF) for the logged-in user |
| `GET` | `/clients` | ADMIN | List customers (paginated) |
| `GET` | `/clients/{id}` | ADMIN | Get a customer |
| `GET` | `/clients/details` | CLIENT | Get the logged-in customer's details |
| `POST` | `/parking-spaces` | ADMIN | Create a parking space |
| `GET` | `/parking-spaces/{code}` | ADMIN | Get a parking space |
| `POST` | `/parking-lots/check-in` | ADMIN | Register a vehicle entry |
| `GET` | `/parking-lots/check-in/{receipt}` | ADMIN, or the client who owns it | Get an open check-in by receipt |
| `PUT` | `/parking-lots/check-out/{receipt}` | ADMIN | Register the exit and charge the fee |
| `GET` | `/parking-lots/cpf/{cpf}` | ADMIN | A customer's parking history |
| `GET` | `/parking-lots` | CLIENT | The logged-in customer's parking history |
| `GET` | `/parking-lots/reports` | CLIENT | The logged-in customer's history as PDF |

Interactive docs: **http://localhost:8080/docs-park.html**

> `cpf` is the Brazilian individual taxpayer ID.

## Tech stack

Java 17 · Spring Boot 3.3 (Web, Data JPA, Security, Validation) · JWT (jjwt) · MySQL · H2 (tests) · JasperReports · springdoc-openapi · ModelMapper · Lombok · JUnit 5 · WebTestClient · Mockito

## Running locally

**Requirements:** Java 17 and MySQL 8 on `localhost:3306` with a database named `demo_park` (credentials in `src/main/resources/application.properties`).

Tokens are signed with a key read from the `JWT_SECRET` environment variable. It must be at least 32 characters; the application refuses to start without it.

```bash
export JWT_SECRET="replace-with-a-long-random-value"
./mvnw spring-boot:run
```

The schema is created automatically on startup.

## Tests

```bash
./mvnw verify
```

- **Integration tests** (`*IT`) start the full application on a random port and call the real HTTP endpoints with `WebTestClient`, using an in-memory H2 database seeded per test with SQL scripts.
- **Unit tests** (`*Test`) cover the PDF report service and JWT signing/validation.

GitHub Actions runs everything on every push.

## Known limitations / next steps

Being honest about what's not done yet:

- [ ] The PDF report query uses MySQL-specific SQL, so it isn't covered by the H2 integration tests (the service itself is unit-tested)
- [ ] No Docker Compose setup for MySQL yet
