// Сессия пользователя в браузере: токены и данные пользователя лежат в localStorage.
// localStorage живёт, пока его не очистят, поэтому вход сохраняется после закрытия вкладки.

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";
const USER_KEY = "user";

function saveSession(tokens) {
    localStorage.setItem(ACCESS_TOKEN_KEY, tokens.access_token);
    localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refresh_token);
    localStorage.setItem(USER_KEY, JSON.stringify(tokens.user));
}

function clearSession() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
}

function getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
}

function getCurrentUser() {
    const saved = localStorage.getItem(USER_KEY);
    return saved === null ? null : JSON.parse(saved);
}

// Роль нужна только чтобы спрятать лишние кнопки. Права всё равно проверяет сервер:
// если USER вызовет удаление вручную, сервер ответит 403
function isAdmin() {
    const user = getCurrentUser();
    return user !== null && user.role === "ADMIN";
}

function goToLogin() {
    const here = window.location.pathname + window.location.search;
    window.location.href = "login.html?next=" + encodeURIComponent(here);
}

// Вызывается в начале каждой закрытой страницы: без входа — на страницу входа
function requireLogin() {
    if (getAccessToken() === null) {
        goToLogin();
        return false;
    }
    return true;
}

// Шапка: кто вошёл, кнопка «Выйти»; элементы с классом admin-only видит только ADMIN
function setupHeader() {
    const user = getCurrentUser();
    const panel = document.querySelector("#user-panel");

    if (user !== null && panel !== null) {
        document.querySelector("#user-name").textContent = user.username + " (" + user.role + ")";
        document.querySelector("#logout-button").addEventListener("click", onLogoutClick);
        panel.hidden = false;
    }

    document.querySelectorAll(".admin-only").forEach(function (element) {
        element.hidden = !isAdmin();
    });
}

async function onLogoutClick() {
    await logout();
    window.location.href = "login.html";
}
