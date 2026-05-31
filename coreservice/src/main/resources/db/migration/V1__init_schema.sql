CREATE SCHEMA IF NOT EXISTS core;

CREATE TABLE core.chatbots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    avatar_url TEXT,
    system_prompt TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    language VARCHAR(10) NOT NULL DEFAULT 'VI',
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_chatbots_user_id ON core.chatbots(user_id);

CREATE TABLE core.chatbot_situations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chatbot_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    instruction TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_chatbot_situation FOREIGN KEY (chatbot_id) REFERENCES core.chatbots(id) ON DELETE CASCADE
);

CREATE INDEX idx_situations_chatbot_id ON core.chatbot_situations(chatbot_id);

CREATE TABLE core.knowledge_sources (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chatbot_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    source_type VARCHAR(20) NOT NULL, -- FILE, URL, TEXT
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED', -- CREATED, PROCESSING, COMPLETED, FAILED
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_knowledge_chatbot FOREIGN KEY (chatbot_id) REFERENCES core.chatbots(id) ON DELETE CASCADE
);

CREATE INDEX idx_knowledge_chatbot_id ON core.knowledge_sources(chatbot_id);

CREATE TABLE core.source_files (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url TEXT NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    CONSTRAINT fk_source_file FOREIGN KEY (source_id) REFERENCES core.knowledge_sources(id) ON DELETE CASCADE,
    CONSTRAINT uk_source_file UNIQUE (source_id)
);

CREATE TABLE core.source_urls (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_id BIGINT NOT NULL,
    url TEXT NOT NULL,
    content TEXT,
    CONSTRAINT fk_source_url FOREIGN KEY (source_id) REFERENCES core.knowledge_sources(id) ON DELETE CASCADE,
    CONSTRAINT uk_source_url UNIQUE (source_id)
);

CREATE TABLE core.source_texts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    CONSTRAINT fk_source_text FOREIGN KEY (source_id) REFERENCES core.knowledge_sources(id) ON DELETE CASCADE,
    CONSTRAINT uk_source_text UNIQUE (source_id)
);
