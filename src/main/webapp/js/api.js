const API_URL = "/api/requests";
const AUTH_URL = "/api/auth";
const QUERY_THRESHOLD = 3;

const FILTER_NAMES = ["full_name", "group", "dormitory", "room", "is_foreign", "living"];

const DORMITORIES = {
    sg: "Студенческий городок",
    alp: "Общежитие № 2 (Альпийская)",
    bel: "Общежитие № 3 (Белорусская)",
    len: "Общежитие № 4 (Ленсовета)",
    msg: "Межвузовский студенческий городок"
};

class ApiError extends Error {
    constructor(status, message, details) {
        super(message);
        this.status = status;
        this.details = details;
    }
}

function getDormitoryName(code) {
    return DORMITORIES[code] || "—";
}

async function readError(response) {
    try {
        const data = await response.json();
        return new ApiError(response.status, data.error.message, data.error.details);
    } catch (error) {
        return new ApiError(response.status, "Ошибка сервера (код " + response.status + ")", []);
    }
}

async function send(method, url, body, token) {
    const options = { method: method, headers: {} };

    if (body !== undefined) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    if (token) {
        options.headers["Authorization"] = "Bearer " + token;
    }

    try {
        return await fetch(url, options);
    } catch (error) {
        throw new ApiError(0, "Сервер недоступен. Проверьте, что он запущен, и обновите страницу", []);
    }
}

let refreshInProgress = null;

function refreshTokens() {
    if (refreshInProgress === null) {
        refreshInProgress = doRefresh().finally(function () {
            refreshInProgress = null;
        });
    }
    return refreshInProgress;
}

async function doRefresh() {
    const refreshToken = getRefreshToken();

    if (refreshToken === null) {
        return false;
    }

    const response = await send("POST", AUTH_URL + "/refresh", { refresh_token: refreshToken });

    if (!response.ok) {
        return false;
    }

    saveSession(await response.json());
    return true;
}

async function request(method, url, body) {
    let response = await send(method, url, body, getAccessToken());

    if (response.status === 401 && await refreshTokens()) {
        response = await send(method, url, body, getAccessToken());
    }

    if (response.status === 401) {
        clearSession();
        goToLogin();
        throw await readError(response);
    }

    if (!response.ok) {
        throw await readError(response);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

async function publicRequest(method, url, body) {
    const response = await send(method, url, body);

    if (!response.ok) {
        throw await readError(response);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

async function login(username, password) {
    saveSession(await publicRequest("POST", AUTH_URL + "/login", { username: username, password: password }));
}

async function register(data) {
    return publicRequest("POST", AUTH_URL + "/register", data);
}

async function logout() {
    const refreshToken = getRefreshToken();

    try {
        if (refreshToken !== null) {
            await publicRequest("POST", AUTH_URL + "/logout", { refresh_token: refreshToken });
        }
    } catch (error) {
    }

    clearSession();
}

function studentUrl(isu) {
    return API_URL + "/" + encodeURIComponent(isu);
}

async function getStudents(params) {
    const active = {};

    Object.keys(params).forEach(function (name) {
        if (params[name] !== "" && params[name] !== null && params[name] !== undefined) {
            active[name] = params[name];
        }
    });

    const filterCount = Object.keys(active).filter(function (name) {
        return FILTER_NAMES.indexOf(name) !== -1;
    }).length;

    if (filterCount > QUERY_THRESHOLD) {
        return request("QUERY", API_URL, active);
    }

    const query = new URLSearchParams(active).toString();

    return request("GET", query === "" ? API_URL : API_URL + "?" + query);
}

async function getStudent(isu) {
    return request("GET", studentUrl(isu));
}

async function createStudent(data) {
    return request("POST", API_URL, data);
}

async function updateStudent(isu, data) {
    return request("PUT", studentUrl(isu), data);
}

async function deleteStudent(isu) {
    return request("DELETE", studentUrl(isu));
}

function getIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (id === null || id === "") {
        return null;
    }

    return id;
}

function formatDate(isoDate) {
    if (!isoDate) {
        return "";
    }

    const parts = isoDate.split("-");

    if (parts.length !== 3) {
        return isoDate;
    }

    return parts[2] + "." + parts[1] + "." + parts[0];
}

function formatPeriod(from, to) {
    const start = formatDate(from);
    const end = formatDate(to);

    if (start === "" && end === "") {
        return "—";
    }

    if (end === "") {
        return start + " — по настоящее время";
    }

    if (start === "") {
        return end;
    }

    return start + " — " + end;
}
