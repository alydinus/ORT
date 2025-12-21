-- liquibase formatted sql

-- changeset ort:001-initial
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    username   VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN      NOT NULL DEFAULT FALSE,
    is_locked  BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT users_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

INSERT INTO roles (name)
VALUES ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name)
VALUES ('ROLE_MODERATOR')
ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name)
VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- changeset ort:002-testing
CREATE TABLE IF NOT EXISTS questions
(
    id            BIGSERIAL PRIMARY KEY,
    question_text VARCHAR(255),
    question_type VARCHAR(50),
    points        INT
);

CREATE TABLE IF NOT EXISTS answers
(
    id          BIGSERIAL PRIMARY KEY,
    answer_text VARCHAR(255),
    is_correct  BOOLEAN NOT NULL,
    question_id BIGINT  NOT NULL,
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE IF NOT EXISTS tests
(
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255),
    description      TEXT,
    duration_minutes INT,
    is_active        BOOLEAN DEFAULT TRUE,
    author_id        BIGINT,
    CONSTRAINT fk_tests_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS test_questions
(
    test_id     BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    CONSTRAINT test_questions_pkey PRIMARY KEY (test_id, question_id),
    CONSTRAINT fk_test_questions_test FOREIGN KEY (test_id) REFERENCES tests (id),
    CONSTRAINT fk_test_questions_question FOREIGN KEY (question_id) REFERENCES questions (id)
);

CREATE TABLE IF NOT EXISTS test_results
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    score   INT,
    date    TIMESTAMP,
    CONSTRAINT fk_test_results_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_test_results_test FOREIGN KEY (test_id) REFERENCES tests (id)
);

-- changeset ort:003-articles
CREATE TABLE IF NOT EXISTS categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT
);

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS articles
(
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(512),
    content      TEXT,
    html         TEXT,
    author_id    BIGINT,
    created_at   TIMESTAMP,
    views        BIGINT      DEFAULT 0,
    is_published BOOLEAN     DEFAULT FALSE,
    category_id  BIGINT,
    status       VARCHAR(32) DEFAULT 'PENDING',
    CONSTRAINT fk_articles_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS articles_tags
(
    article_entity_id BIGINT NOT NULL,
    tags_id           BIGINT NOT NULL,
    CONSTRAINT articles_tags_pkey PRIMARY KEY (article_entity_id, tags_id),
    CONSTRAINT fk_articles_tags_article FOREIGN KEY (article_entity_id) REFERENCES articles (id),
    CONSTRAINT fk_articles_tags_tag FOREIGN KEY (tags_id) REFERENCES tags (id)
);

CREATE TABLE IF NOT EXISTS tests_tags
(
    test_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    CONSTRAINT tests_tags_pkey PRIMARY KEY (test_id, tag_id),
    CONSTRAINT fk_tests_tags_test FOREIGN KEY (test_id) REFERENCES tests (id),
    CONSTRAINT fk_tests_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id                BIGSERIAL PRIMARY KEY,
    comment           TEXT,
    author_id         BIGINT,
    article_entity_id BIGINT,
    CONSTRAINT fk_comments_article FOREIGN KEY (article_entity_id) REFERENCES articles (id)
);

CREATE TABLE IF NOT EXISTS reactions
(
    id                BIGSERIAL PRIMARY KEY,
    reaction_value    VARCHAR(50),
    author_id         BIGINT,
    article_entity_id BIGINT,
    CONSTRAINT fk_reactions_article FOREIGN KEY (article_entity_id) REFERENCES articles (id)
);

-- changeset ort:004-seed-users-tests
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_username ON users (username);

INSERT INTO users (email, password, username, is_enabled, is_locked)
VALUES ('admin@ort.local', '$2a$12$sy1221I/9/B7k5T00U00muKvZx6zsYnSAW8BXWq1Hc0bN9177LhJm', 'admin', TRUE, FALSE),
       ('moderator@ort.local', '$2a$12$sy1221I/9/B7k5T00U00muKvZx6zsYnSAW8BXWq1Hc0bN9177LhJm', 'moderator', TRUE,
        FALSE),
       ('user@ort.local', '$2a$12$sy1221I/9/B7k5T00U00muKvZx6zsYnSAW8BXWq1Hc0bN9177LhJm', 'user', TRUE, FALSE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email = 'admin@ort.local'
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_MODERATOR'
WHERE u.email = 'moderator@ort.local'
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_USER'
WHERE u.email = 'user@ort.local'
ON CONFLICT DO NOTHING;

CREATE UNIQUE INDEX IF NOT EXISTS uk_tests_title ON tests (title);
CREATE UNIQUE INDEX IF NOT EXISTS uk_questions_text ON questions (question_text);

INSERT INTO tests (title, description, duration_minutes, is_active, author_id)
SELECT 'Тренировочный тест: Математика', 'Базовый тест для проверки знаний.', 20, TRUE, u.id
FROM users u
WHERE u.email = 'moderator@ort.local'
ON CONFLICT (title) DO NOTHING;

INSERT INTO tests (title, description, duration_minutes, is_active, author_id)
SELECT 'Тренировочный тест: Русский язык', 'Небольшой тест по орфографии и пунктуации.', 15, TRUE, u.id
FROM users u
WHERE u.email = 'moderator@ort.local'
ON CONFLICT (title) DO NOTHING;

INSERT INTO questions (question_text, question_type, points)
VALUES ('2 + 2 = ?', 'SINGLE', 1),
       ('Выберите правильное написание: ...', 'SINGLE', 1)
ON CONFLICT (question_text) DO NOTHING;

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '4', TRUE, q.id
FROM questions q
WHERE q.question_text = '2 + 2 = ?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '4');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '5', FALSE, q.id
FROM questions q
WHERE q.question_text = '2 + 2 = ?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '5');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'прЕвратить', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Выберите правильное написание: ...'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'прЕвратить');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'прИвратить', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите правильное написание: ...'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'прИвратить');

INSERT INTO test_questions (test_id, question_id)
SELECT t.id, q.id
FROM tests t,
     questions q
WHERE t.title = 'Тренировочный тест: Математика'
  AND q.question_text = '2 + 2 = ?'
ON CONFLICT DO NOTHING;

INSERT INTO test_questions (test_id, question_id)
SELECT t.id, q.id
FROM tests t,
     questions q
WHERE t.title = 'Тренировочный тест: Русский язык'
  AND q.question_text = 'Выберите правильное написание: ...'
ON CONFLICT DO NOTHING;

-- changeset ort:005-seed-more-tests
INSERT INTO tags (name)
VALUES ('алгебра'),
       ('геометрия'),
       ('орфография'),
       ('пунктуация'),
       ('логика'),
       ('внимательность')
ON CONFLICT (name) DO NOTHING;

INSERT INTO tests (title, description, duration_minutes, is_active, author_id)
SELECT 'Подготовка к ORT: Математика (расширенный)',
       'Расширенный набор вопросов по базовой математике, логике и внимательности.',
       35,
       TRUE,
       u.id
FROM users u
WHERE u.email = 'moderator@ort.local'
ON CONFLICT (title) DO NOTHING;

INSERT INTO tests (title, description, duration_minutes, is_active, author_id)
SELECT 'Подготовка к ORT: Русский язык (расширенный)',
       'Расширенный набор вопросов по орфографии и пунктуации.',
       30,
       TRUE,
       u.id
FROM users u
WHERE u.email = 'moderator@ort.local'
ON CONFLICT (title) DO NOTHING;

INSERT INTO tests_tags (test_id, tag_id)
SELECT t.id, tg.id
FROM tests t
         JOIN tags tg ON tg.name IN ('алгебра', 'геометрия', 'логика', 'внимательность')
WHERE t.title = 'Подготовка к ORT: Математика (расширенный)'
ON CONFLICT DO NOTHING;

INSERT INTO tests_tags (test_id, tag_id)
SELECT t.id, tg.id
FROM tests t
         JOIN tags tg ON tg.name IN ('орфография', 'пунктуация')
WHERE t.title = 'Подготовка к ORT: Русский язык (расширенный)'
ON CONFLICT DO NOTHING;

INSERT INTO questions (question_text, question_type, points)
VALUES ('12 / 3 = ?', 'SINGLE', 1),
       ('Выберите простые числа', 'MULTIPLE', 2),
       ('Сколько градусов в развернутом угле?', 'SINGLE', 1),
       ('Продолжите последовательность: 2, 4, 8, 16, ...', 'SINGLE', 1),
       ('Что из перечисленного является треугольником?', 'SINGLE', 1),
       ('Выберите слова с мягким знаком', 'MULTIPLE', 2),
       ('Где нужна запятая: "Когда наступила весна ... стало тепло"', 'SINGLE', 1),
       ('Выберите правильное ударение', 'SINGLE', 1)
ON CONFLICT (question_text) DO NOTHING;

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '4', TRUE, q.id
FROM questions q
WHERE q.question_text = '12 / 3 = ?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '4');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '5', FALSE, q.id
FROM questions q
WHERE q.question_text = '12 / 3 = ?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '5');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '2', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите простые числа'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '2');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '3', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите простые числа'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '3');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '4', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Выберите простые числа'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '4');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '5', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите простые числа'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '5');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '180', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Сколько градусов в развернутом угле?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '180');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '90', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Сколько градусов в развернутом угле?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '90');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '32', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Продолжите последовательность: 2, 4, 8, 16, ...'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '32');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT '24', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Продолжите последовательность: 2, 4, 8, 16, ...'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = '24');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'Фигура с тремя сторонами', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Что из перечисленного является треугольником?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'Фигура с тремя сторонами');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'Фигура с четырьмя сторонами', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Что из перечисленного является треугольником?'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'Фигура с четырьмя сторонами');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'пальто', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Выберите слова с мягким знаком'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'пальто');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'конь', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите слова с мягким знаком'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'конь');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'ночь', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите слова с мягким знаком'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'ночь');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'дома', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Выберите слова с мягким знаком'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'дома');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'нужна', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Где нужна запятая: "Когда наступила весна ... стало тепло"'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'нужна');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'не нужна', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Где нужна запятая: "Когда наступила весна ... стало тепло"'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'не нужна');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'звонИт', TRUE, q.id
FROM questions q
WHERE q.question_text = 'Выберите правильное ударение'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'звонИт');

INSERT INTO answers (answer_text, is_correct, question_id)
SELECT 'звОнит', FALSE, q.id
FROM questions q
WHERE q.question_text = 'Выберите правильное ударение'
  AND NOT EXISTS (SELECT 1 FROM answers a WHERE a.question_id = q.id AND a.answer_text = 'звОнит');

INSERT INTO test_questions (test_id, question_id)
SELECT t.id, q.id
FROM tests t, questions q
WHERE t.title = 'Подготовка к ORT: Математика (расширенный)'
  AND q.question_text IN (
      '12 / 3 = ?',
      'Выберите простые числа',
      'Сколько градусов в развернутом угле?',
      'Продолжите последовательность: 2, 4, 8, 16, ...',
      'Что из перечисленного является треугольником?'
    )
ON CONFLICT DO NOTHING;

INSERT INTO test_questions (test_id, question_id)
SELECT t.id, q.id
FROM tests t, questions q
WHERE t.title = 'Подготовка к ORT: Русский язык (расширенный)'
  AND q.question_text IN (
      'Выберите слова с мягким знаком',
      'Где нужна запятая: "Когда наступила весна ... стало тепло"',
      'Выберите правильное ударение'
    )
ON CONFLICT DO NOTHING;

-- changeset ort:006-test-themes
CREATE TABLE IF NOT EXISTS test_themes
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE tests
    ADD COLUMN IF NOT EXISTS theme_id BIGINT;

ALTER TABLE tests
    ADD CONSTRAINT fk_tests_theme
        FOREIGN KEY (theme_id) REFERENCES test_themes (id);

INSERT INTO test_themes (name)
VALUES ('математика'),
       ('русский язык')
ON CONFLICT (name) DO NOTHING;

UPDATE tests
SET theme_id = tt.id
FROM test_themes tt
WHERE lower(tt.name) = 'математика'
  AND title ILIKE '%математика%'
  AND theme_id IS NULL;

UPDATE tests
SET theme_id = tt.id
FROM test_themes tt
WHERE lower(tt.name) = 'русский язык'
  AND title ILIKE '%русский%'
  AND theme_id IS NULL;
