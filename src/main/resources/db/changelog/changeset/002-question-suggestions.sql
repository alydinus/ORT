--liquibase formatted sql

--changeset ort:002-question-suggestions
CREATE TABLE question_suggestions (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    question_text VARCHAR(2000) NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    points INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_question_suggestions_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE INDEX idx_question_suggestions_status ON question_suggestions(status);
CREATE INDEX idx_question_suggestions_author ON question_suggestions(author_id);

CREATE TABLE question_suggestion_answers (
    id BIGSERIAL PRIMARY KEY,
    suggestion_id BIGINT NOT NULL,
    answer_text VARCHAR(2000) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    CONSTRAINT fk_question_suggestion_answers_suggestion FOREIGN KEY (suggestion_id) REFERENCES question_suggestions (id) ON DELETE CASCADE
);

CREATE INDEX idx_question_suggestion_answers_suggestion ON question_suggestion_answers(suggestion_id);

