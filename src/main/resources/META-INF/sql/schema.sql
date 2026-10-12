-- Схема базы данных. EclipseLink выполняет этот файл при каждом старте приложения (persistence.xml).
-- IF NOT EXISTS не даёт пересоздать уже существующие таблицы и потерять данные.
-- EclipseLink читает скрипт построчно, поэтому каждая команда записана в одну строку.

-- Студенты. Первичный ключ — номер ИСУ. check_out = NULL — студент ещё проживает
CREATE TABLE IF NOT EXISTS students (isu_id VARCHAR(6) PRIMARY KEY, full_name VARCHAR(100) NOT NULL, group_name VARCHAR(5) NOT NULL, dormitory VARCHAR(3) NOT NULL, room VARCHAR(4) NOT NULL, check_in DATE NOT NULL, check_out DATE, is_foreign BOOLEAN NOT NULL, notes VARCHAR(500) NOT NULL DEFAULT '');

-- Пользователи. Логин и почта уникальны, пароль — только хеш BCrypt (60 символов), роль — ADMIN или USER
CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(32) NOT NULL UNIQUE, email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(60) NOT NULL, role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'USER')), created_at TIMESTAMP NOT NULL);

-- Выданные refresh-токены. Внешний ключ user_id -> users.id: у одного пользователя много токенов (один-ко-многим)
CREATE TABLE IF NOT EXISTS refresh_tokens (id BIGSERIAL PRIMARY KEY, jti VARCHAR(36) NOT NULL UNIQUE, user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE, expires_at TIMESTAMP NOT NULL, revoked BOOLEAN NOT NULL DEFAULT FALSE);

-- Студенты для примера. ON CONFLICT DO NOTHING: если студент с таким ИСУ уже есть, строка пропускается
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('412345', 'Иванов Иван Иванович', 'M3302', 'alp', '1205', '2024-09-01', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('412346', 'Петрова Анна Сергеевна', 'M3301', 'alp', '1207', '2024-09-01', NULL, FALSE, 'Староста этажа') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('410020', 'Гарсия Лопес Карлос', 'M3301', 'alp', '1301', '2024-09-01', '2025-01-31', TRUE, 'Выселился после первого семестра') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('398712', 'Римский-Корсаков Николай Андреевич', 'P3213', 'sg', '2104', '2023-08-28', '2025-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('405501', 'Ли Вэй', 'K3221', 'bel', '3112', '2025-08-30', NULL, TRUE, 'Студент по обмену') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('401177', 'О''Брайен Шон', 'M3302', 'len', '4016', '2024-09-02', NULL, TRUE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('389944', 'Смирнова Мария Олеговна', 'R3340', 'msg', '2210', '2022-09-01', '2024-07-15', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('407788', 'Кузнецов Дмитрий Павлович', 'P3213', 'bel', '3305', '2025-09-01', NULL, FALSE, 'Нужна справка о проживании') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413000', 'Соколов Артём Игоревич', 'M3301', 'msg', '2129', '2024-08-13', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413037', 'Волкова Екатерина Дмитриевна', 'K3221', 'sg', '4038', '2022-08-02', '2023-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413074', 'Морозов Илья Сергеевич', 'M3302', 'msg', '1199', '2025-08-27', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413111', 'Новикова Софья Андреевна', 'P3110', 'alp', '5142', '2025-08-08', '2026-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413148', 'Фёдоров Максим Алексеевич', 'M3302', 'sg', '4121', '2022-09-18', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413185', 'Лебедева Полина Викторовна', 'M3203', 'msg', '7117', '2022-08-20', '2023-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413222', 'Козлов Никита Романович', 'M3302', 'alp', '2194', '2025-09-10', '2026-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413259', 'Егорова Алина Павловна', 'R3340', 'sg', '2176', '2025-09-24', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413296', 'Павлов Кирилл Евгеньевич', 'K3221', 'sg', '2189', '2023-09-05', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413333', 'Сидорова Дарья Михайловна', 'K3221', 'msg', '8031', '2024-09-23', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413370', 'Ахмедов Тимур Рашидович', 'M3301', 'bel', '8106', '2024-09-23', NULL, TRUE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413407', 'Нгуен Ван Минь', 'R3340', 'sg', '8023', '2024-08-15', NULL, TRUE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413444', 'Орлова Вероника Олеговна', 'K3221', 'len', '2067', '2024-08-24', '2025-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413481', 'Белов Георгий Андреевич', 'P3110', 'msg', '5145', '2025-09-05', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413518', 'Шмидт Анна-Мария', 'M3302', 'alp', '4017', '2025-08-05', '2026-06-30', TRUE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413555', 'Захаров Степан Ильич', 'K3221', 'msg', '6209', '2023-09-10', '2024-06-30', FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
INSERT INTO students (isu_id, full_name, group_name, dormitory, room, check_in, check_out, is_foreign, notes) VALUES ('413592', 'Григорьева Ульяна Сергеевна', 'R3340', 'sg', '8186', '2024-08-23', NULL, FALSE, '') ON CONFLICT (isu_id) DO NOTHING;
