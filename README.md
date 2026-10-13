# Лабораторная работа №3. Backend на Java с авторизацией

Система управления студентами общежития из лабораторной работы №2. Backend перенесён
на Java. Вариант — **Jakarta EE + EclipseLink**.

Что добавилось по сравнению с лабораторной работой №2:

- данные хранятся в PostgreSQL, доступ к ним идёт через JPA (реализация — EclipseLink);
- пользователи, регистрация и вход по JWT (access- и refresh-токены);
- роли: **ADMIN** выполняет все операции, **USER** только читает;
- в списке студентов есть сортировка и страницы, для полного изменения есть метод PUT;
- API описан в Swagger UI, сценарии собраны в коллекцию Postman;
- сервер пишет логи, настройки вынесены в файл `.env`.

## Технологии

| Что | Чем |
|---|---|
| Язык | Java 21 |
| Сервер приложений | Open Liberty (Jakarta EE 10, MicroProfile) |
| REST API | Jakarta REST (JAX-RS) |
| Внедрение зависимостей | CDI (`@Inject`, `@ApplicationScoped`) |
| База данных | PostgreSQL 16 |
| ORM | JPA, реализация EclipseLink |
| Проверка данных | Bean Validation (`@NotNull`, `@Pattern`, `@Size`) |
| JSON | JSON-B и JSON-P |
| JWT | библиотека `com.auth0:java-jwt`, подпись HS256 |
| Пароли | BCrypt (библиотека `jbcrypt`) |
| Документация API | MicroProfile OpenAPI, Swagger UI |
| Сборка | Maven 3.9 |
| Клиент | HTML, CSS и JavaScript без фреймворков (страницы из лабораторной работы №2) |

---

## Запуск

Нужны три программы: **JDK 21**, **Maven** и **PostgreSQL 16**. Open Liberty скачивается сам
при первом запуске.

### 1. Установите JDK 21 и Maven (один раз)

**Windows:**

1. Скачайте установщик Eclipse Temurin 21 (`.msi`, x64) с <https://adoptium.net/temurin/releases/?version=21>.
2. В установщике включите пункты **Set JAVA_HOME variable** и **Add to PATH**.
3. Скачайте архив Maven (`apache-maven-3.9.x-bin.zip`) с <https://maven.apache.org/download.cgi> и распакуйте его.
4. Добавьте папку `bin` из распакованного архива в переменную `Path`.
5. Откройте **новое** окно PowerShell и проверьте: `java -version` и `mvn -v`. В ответе должна быть строка `21`.

**Linux / macOS:** установите JDK 21 и Maven из пакетного менеджера и проверьте `java -version` и `mvn -v`.

### 2. Скачайте проект и создайте `.env`

```
git clone https://github.com/fafakaj/web_lab_3.git
cd web_lab_3
```

Создайте в папке проекта файл `.env` с таким содержимым:

```
HTTP_PORT=8080

DB_HOST=localhost
DB_PORT=5432
DB_NAME=students
DB_USER=students
DB_PASSWORD=students

JWT_SECRET=change-me-to-a-random-string-of-32-chars-or-more
JWT_ACCESS_MINUTES=15
JWT_REFRESH_DAYS=7

ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=admin12345
```

Замените `JWT_SECRET` на свою случайную строку не короче 32 символов. Остальные значения подходят для запуска на своём компьютере. Что означает каждый параметр — в разделе [Настройки](#настройки-env).

### 3. Подготовьте базу данных (один раз)

1. Установите PostgreSQL 16 с <https://www.postgresql.org/download/>. Запомните пароль пользователя `postgres`.
2. Создайте пользователя и базу с именами и паролем из `.env` (по умолчанию все три — `students`):

```
psql -U postgres -c "CREATE ROLE students LOGIN PASSWORD 'students';"
psql -U postgres -c "CREATE DATABASE students OWNER students;"
```

Таблицы создавать не нужно: приложение создаёт их само при первом запуске (скрипт `schema.sql`).

### 4. Запустите приложение

```
mvn liberty:dev
```

Первый запуск занимает несколько минут: скачиваются Open Liberty и библиотеки.
Приложение готово, когда в консоли появилась строка:

```
The defaultServer server is ready to run a smarter planet.
```

`liberty:dev` — режим разработки: после правки кода сервер сам пересобирает и перезапускает приложение.
Чтобы остановить сервер, нажмите `Ctrl+C`.

### 5. Откройте в браузере

| Что | Адрес |
|---|---|
| Интерфейс | <http://localhost:8080/> |
| Swagger UI | <http://localhost:8080/openapi/ui/> |
| Описание API (OpenAPI) | <http://localhost:8080/openapi> |

**Учётные записи:**

- **ADMIN** создаётся при первом запуске из `.env`: логин `admin`, пароль `admin12345`.
- **USER** — любой пользователь, который зарегистрировался на странице «Регистрация» или через `POST /api/auth/register`.

После первого запуска база пустая. Добавьте студентов на странице «Добавить студента» или через `POST /api/requests`.

---

## Структура проекта

```
web_lab_3/
  pom.xml                     зависимости и сборка (Maven), плагин Open Liberty
  postman/                    коллекция запросов Postman
  src/main/
    liberty/config/server.xml настройки сервера: функции Jakarta EE, порт, подключение к БД, логи
    resources/
      META-INF/persistence.xml  настройки JPA: источник данных, скрипт схемы
      META-INF/sql/schema.sql   таблицы, первичные и внешние ключи
    java/ru/itmo/students/
      StudentsApplication.java  точка входа REST API (/api) и описание для Swagger
      resource/     контроллеры: HTTP-запрос -> вызов сервиса -> ответ
      service/      бизнес-логика: правила дат, уникальность, вход и токены
      repository/   доступ к БД через EntityManager (JPA)
      entity/       сущности JPA — строки таблиц students, users, refresh_tokens
      dto/          форматы запросов и ответов API (отделены от сущностей)
      security/     JWT, BCrypt, фильтры аутентификации и авторизации
      exception/    исключения приложения и их перевод в ответы с ошибкой
      config/       настройка JSON, лог запросов, данные при старте
    webapp/         клиент: HTML, CSS, JS
```

Слои вызывают друг друга только сверху вниз:
`resource` → `service` → `repository` → БД. Ресурс не обращается к БД, репозиторий не знает про HTTP.

---

## API

Все тела запросов и ответов — JSON. Защищённые адреса требуют заголовок

```
Authorization: Bearer <access_token>
```

### Авторизация — `/api/auth`

| Метод | Путь | Кто может | Тело | Ответ |
|---|---|---|---|---|
| POST | `/api/auth/register` | все | `username`, `email`, `password` | 201, пользователь с ролью USER |
| POST | `/api/auth/login` | все | `username`, `password` | 200, токены |
| POST | `/api/auth/refresh` | все | `refresh_token` | 200, новая пара токенов |
| POST | `/api/auth/logout` | все | `refresh_token` | 204, refresh-токен отозван |
| GET | `/api/auth/me` | USER, ADMIN | — | 200, текущий пользователь |

Ответ на вход и обновление:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
  "token_type": "Bearer",
  "expires_in": 900,
  "user": {"id": 1, "username": "admin", "email": "admin@example.com", "role": "ADMIN"}
}
```

- **access-токен** живёт 15 минут. Его передают в заголовке `Authorization` к каждому запросу.
- **refresh-токен** живёт 7 дней. Когда access-токен истёк (ответ 401), клиент отправляет
  refresh-токен на `/api/auth/refresh` и получает новую пару без ввода логина и пароля.
- Каждый refresh-токен работает **один раз**: после обмена старый отзывается. Если пришёл уже
  использованный токен, сервер отзывает все токены этого пользователя — нужно войти заново.

Правила регистрации: логин — от 3 до 32 символов (латинские буквы, цифры, `_`), почта — адрес
вида `name@example.com`, пароль — от 8 до 64 символов, хотя бы одна буква и одна цифра.
Логин и почта уникальны и не зависят от регистра.

### Студенты — `/api/requests`

Путь и формат студента — как в лабораторной работе №2.

| Метод | Путь | Кто может | Тело | Успех |
|---|---|---|---|---|
| GET | `/api/requests` | USER, ADMIN | — | 200, страница списка |
| QUERY | `/api/requests` | USER, ADMIN | фильтры | 200, страница списка |
| GET | `/api/requests/{isu_id}` | USER, ADMIN | — | 200, студент |
| POST | `/api/requests` | ADMIN | студент целиком | 201, созданный студент, заголовок `Location` |
| PUT | `/api/requests/{isu_id}` | ADMIN | все поля, кроме ИСУ | 200, полное изменение |
| PATCH | `/api/requests/{isu_id}` | ADMIN | только меняемые поля | 200, частичное изменение |
| DELETE | `/api/requests/{isu_id}` | ADMIN | — | 204, без тела |

**Студент:**

```json
{
  "isu_id": "412345",
  "full_name": "Иванов Иван Иванович",
  "group": "M3301",
  "dormitory": "alp",
  "room": "1205",
  "check_in": "2024-09-01",
  "check_out": null,
  "is_foreign": false,
  "notes": ""
}
```

`dormitory` — код общежития: `sg`, `alp`, `bel`, `len`, `msg`. `check_out: null` — студент ещё проживает.

**Параметры списка** (все необязательные; в GET — query-параметры, в QUERY — поля JSON-тела):

| Параметр | Что делает | По умолчанию |
|---|---|---|
| `full_name` | поиск по части ФИО без учёта регистра | — |
| `group`, `dormitory`, `room` | точное совпадение | — |
| `is_foreign` | `true` / `false` | — |
| `living` | `true` — проживает (`check_out` пустой), `false` — выселен | — |
| `sort` | поле сортировки: `isu_id`, `full_name`, `group`, `dormitory`, `room`, `check_in`, `check_out` | `full_name` |
| `order` | `asc` — по возрастанию, `desc` — по убыванию | `asc` |
| `page` | номер страницы, с 1 | 1 |
| `size` | записей на странице, от 1 до 100 | 10 |

Пример: `GET /api/requests?group=M3301&sort=check_in&order=desc&page=2&size=5`

**Ответ списка:**

```json
{
  "items": [ {"isu_id": "412346", "full_name": "Петрова Анна Сергеевна", "...": "..."} ],
  "page": 2,
  "size": 5,
  "total": 25
}
```

`total` — сколько студентов подходит под фильтры всего. Число страниц = `total / size`, округлённое вверх.

**PATCH** работает по правилам JSON Merge Patch: поля из тела заменяют текущие, `null` очищает поле.
Например, `{"check_out": null}` — студент снова проживает. ИСУ менять нельзя.

### Ошибки

Все ошибки приходят в одном формате, как в лабораторной работе №2:

```json
{
  "error": {
    "status": 422,
    "code": "VALIDATION_ERROR",
    "message": "Данные не прошли проверку",
    "details": [
      {"field": "room", "message": "Комната — ровно 4 цифры, первая не 0, например 1205"}
    ]
  }
}
```

| Код | `code` | Когда |
|---|---|---|
| 400 | `BAD_REQUEST` | тело не JSON-объект, тела нет, пустой PATCH |
| 401 | `UNAUTHORIZED`, `INVALID_TOKEN`, `TOKEN_EXPIRED` | нет токена, токен поддельный или истёк, неверный логин или пароль |
| 403 | `FORBIDDEN` | токен верный, но роли не хватает прав (USER пытается изменить данные) |
| 404 | `NOT_FOUND` | нет студента с таким ИСУ, неизвестный адрес |
| 405 | `METHOD_NOT_ALLOWED` | метод не поддерживается для адреса |
| 409 | `CONFLICT` | ИСУ, логин или почта уже заняты |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | нет заголовка `Content-Type: application/json` |
| 422 | `VALIDATION_ERROR` | поле неверного формата или типа, неизвестное поле или параметр, нарушено правило дат |
| 500 | `INTERNAL_ERROR` | непредвиденная ошибка сервера; подробности — только в логе сервера |

---

## Проверка

### Swagger UI

1. Откройте <http://localhost:8080/openapi/ui/>.
2. Выполните `POST /api/auth/login` с телом `{"username": "admin", "password": "admin12345"}`.
3. Скопируйте `access_token` из ответа.
4. Нажмите **Authorize** и вставьте токен (без слова `Bearer`).
5. Теперь можно вызывать любые запросы к `/api/requests`.

Метода QUERY в Swagger нет: Swagger знает только стандартные HTTP-методы. QUERY есть в Postman.

### Postman

1. В Postman: **Import** → файл `postman/web_lab_3.postman_collection.json`.
2. Запустите коллекцию целиком (**Run collection**) или запросы по порядку, начиная с папки «1. Авторизация».
   Запросы входа сохраняют токены в переменные коллекции, следующие папки берут их оттуда.

| Папка | Что показывает |
|---|---|
| 1. Авторизация | регистрация (успех, логин занят, неверные данные), вход ADMIN и USER, обновление токена |
| 2. Студенты — ADMIN | фильтры, поиск, сортировка, страницы, один студент, создание, PUT, PATCH, удаление |
| 3. Студенты — USER | USER читает (200), но не изменяет (403) |
| 4. Ошибки | 401 и 404 в едином формате |

У каждого запроса есть автоматическая проверка статуса. Если ваш логин или пароль администратора
в `.env` другие, поменяйте переменные коллекции `adminUsername` и `adminPassword`.
Коллекцию можно запустить и из консоли: `npx newman run postman/web_lab_3.postman_collection.json`.

---

## Логи

Сервер пишет логи в консоль и в файл `target/liberty/wlp/usr/servers/defaultServer/logs/messages.log`:

```
I GET /api/requests?page=2 -> 200 (12 мс, пользователь: admin)
I Создан студент 415001
W Неудачный вход: admin
I POST /api/auth/login -> 401 (85 мс, пользователь: -)
E Необработанная ошибка  (дальше — полная трассировка)
```

- каждый запрос к API: метод, адрес, статус, время, пользователь;
- действия: создание, изменение и удаление студентов, регистрация, вход, выход;
- предупреждения: неудачный вход, повторное использование refresh-токена;
- ошибки 500 — с полной трассировкой (клиент её не получает).

---

## Настройки (`.env`)

Файл `.env` читает Open Liberty.
Плагин Maven копирует его в настройки сервера (`server.env`) при каждом запуске.
`.env` не попадает в git: в нём пароли и секрет JWT. Пример содержимого — в разделе [Запуск](#2-скачайте-проект-и-создайте-env).

| Параметр | Пример | Что задаёт |
|---|---|---|
| `HTTP_PORT` | `8080` | порт приложения |
| `DB_HOST`, `DB_PORT` | `localhost`, `5432` | где работает PostgreSQL |
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | `students` | имя базы, логин и пароль |
| `JWT_SECRET` | длинная случайная строка | секрет для подписи JWT, не короче 32 символов |
| `JWT_ACCESS_MINUTES` | `15` | время жизни access-токена, минуты |
| `JWT_REFRESH_DAYS` | `7` | время жизни refresh-токена, дни |
| `ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` | `admin` | администратор, которого сервер создаёт при первом запуске |

После правки `.env` перезапустите сервер (`Ctrl+C` и снова `mvn liberty:dev`).
Если меняете `DB_*`, создайте в PostgreSQL пользователя и базу с новыми значениями.

---

## Частые проблемы

| Что видно | Что сделать |
|---|---|
| `'java' не является внутренней или внешней командой` | Установите JDK 21 и откройте новое окно PowerShell |
| В логе: `Приложение не смогло подготовить данные при старте` | Проверьте, что PostgreSQL запущен, а пользователь и база из `DB_*` в `.env` созданы |
| `JWT_SECRET ... could not be found` | Нет файла `.env`: создайте его по примеру из раздела «Запуск» |
| `JWT_SECRET должен быть не короче 32 символов` | Сделайте секрет длиннее |
| Порт 8080 или 5432 занят | Поменяйте `HTTP_PORT` или `DB_PORT` в `.env` |
| В браузере постоянно открывается страница входа | Токены устарели — войдите заново |
