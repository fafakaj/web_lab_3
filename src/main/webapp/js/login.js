const loginForm = document.querySelector("#login-form");
const errorBox = document.querySelector("#form-error");
const submitButton = loginForm.querySelector("button[type='submit']");

// Куда вернуться после входа. Только адрес этого же сайта: «/...», но не «//чужой-сайт»
function getNextPage() {
    const next = new URLSearchParams(window.location.search).get("next");

    if (next !== null && next.startsWith("/") && !next.startsWith("//")) {
        return next;
    }

    return "index.html";
}

async function onSubmit(event) {
    event.preventDefault();
    errorBox.textContent = "";
    submitButton.disabled = true;

    try {
        await login(loginForm.elements.username.value, loginForm.elements.password.value);
    } catch (error) {
        submitButton.disabled = false;
        errorBox.textContent = error.message;
        return;
    }

    window.location.href = getNextPage();
}

// Уже вошли — сразу к списку
if (getAccessToken() !== null) {
    window.location.href = getNextPage();
}

loginForm.addEventListener("submit", onSubmit);
