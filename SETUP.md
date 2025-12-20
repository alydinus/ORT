# ORT Platform: руководство по запуску

## Требования
- Java 21
- Maven
- Docker и Docker Compose

## Запуск PostgreSQL и Redis
В папке `ort-1` выполните:

```bash
docker compose up -d
```

Поднимется:
- PostgreSQL: `localhost:5433`, база `ort`, пользователь `postgres`, пароль `1`
- Redis: `localhost:6379`

## Сборка

```bash
./mvnw clean install
```

## Запуск приложения

```bash
./mvnw spring-boot:run
```

## Доступ
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Тестовые пользователи
После поднятия базы (Liquibase заполняет данные автоматически):

- Администратор: `admin` / `admin@ort.local`
- Модератор: `moderator` / `moderator@ort.local`
- Пользователь: `user` / `user@ort.local`

Пароль для всех тестовых пользователей: `OrtTest123!`

Если хотите сменить пароль тестовых пользователей, правьте сид-данные в `src/main/resources/db/changelog/db.changelog-master.sql`.
