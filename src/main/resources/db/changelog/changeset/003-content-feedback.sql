--liquibase formatted sql

--changeset ort:003-content-feedback
CREATE TABLE content_comments (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(32) NOT NULL,
    target_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_content_comments_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE INDEX idx_content_comments_target ON content_comments(target_type, target_id, created_at);
CREATE INDEX idx_content_comments_author ON content_comments(author_id);

CREATE TABLE content_reactions (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(32) NOT NULL,
    target_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    reaction_value VARCHAR(16) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_content_reactions_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE UNIQUE INDEX uk_content_reactions_target_author ON content_reactions(target_type, target_id, author_id);
CREATE INDEX idx_content_reactions_target ON content_reactions(target_type, target_id);
CREATE INDEX idx_content_reactions_author ON content_reactions(author_id);

