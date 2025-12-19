# ORT Platform Setup Guide

## Prerequisites
- Java 21
- Maven
- Docker & Docker Compose

## Database & Redis Setup
The project uses PostgreSQL for the database and Redis for caching and session management.
To start them, run:
```bash
docker-compose up -d
```
This starts:
- **Postgres**: Port 5433 (mapped to 5432 internally), Database: `ort`, User: `postgres`, Password: `1`
- **Redis**: Port 6379 (Default)
- **Mailhog**:
  - SMTP: Port 1025 (for application)
  - Web UI: http://localhost:8025 (view sent emails here)

## Building the Project
```bash
mvn clean install
```

## Running the Application
```bash
java -jar target/ort-0.0.1-SNAPSHOT.jar
```
Or with Maven:
```bash
mvn spring-boot:run
```

## Application Access
- **API Base URL**: `http://localhost:8088` (default Spring Boot port, confirm in application.properties if changed)
- **Swagger UI**: `http://localhost:8088/swagger-ui/index.html` (if enabled)

## Users
- **Admin**: Create via database or registration if seed data exists.
- **Roles**: USER, ADMIN
