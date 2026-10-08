-- Схема базы данных. EclipseLink выполняет этот файл при каждом старте приложения (persistence.xml).
-- IF NOT EXISTS не даёт пересоздать уже существующие таблицы и потерять данные.
-- EclipseLink читает скрипт построчно, поэтому каждая команда записана в одну строку.

-- Студенты. Первичный ключ — номер ИСУ. check_out = NULL — студент ещё проживает
CREATE TABLE IF NOT EXISTS students (isu_id VARCHAR(6) PRIMARY KEY, full_name VARCHAR(100) NOT NULL, group_name VARCHAR(5) NOT NULL, dormitory VARCHAR(3) NOT NULL, room VARCHAR(4) NOT NULL, check_in DATE NOT NULL, check_out DATE, is_foreign BOOLEAN NOT NULL, notes VARCHAR(500) NOT NULL DEFAULT '');

-- Пользователи. Логин и почта уникальны, пароль — только хеш BCrypt (60 символов), роль — ADMIN или USER
CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(32) NOT NULL UNIQUE, email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(60) NOT NULL, role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'USER')), created_at TIMESTAMP NOT NULL);

-- Выданные refresh-токены. Внешний ключ user_id -> users.id: у одного пользователя много токенов (один-ко-многим)
CREATE TABLE IF NOT EXISTS refresh_tokens (id BIGSERIAL PRIMARY KEY, jti VARCHAR(36) NOT NULL UNIQUE, user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE, expires_at TIMESTAMP NOT NULL, revoked BOOLEAN NOT NULL DEFAULT FALSE);
