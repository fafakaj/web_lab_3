const registerForm = document.querySelector("#register-form");
const errorBox = document.querySelector("#form-error");
const submitButton = registerForm.querySelector("button[type='submit']");

const FIELD_NAMES = ["username", "email", "password"];

function clearErrors() {
    errorBox.textContent = "";

    FIELD_NAMES.concat(["password-repeat"]).forEach(function (name) {
        document.querySelector("#error-" + name).textContent = "";
        document.querySelector("#" + name).classList.remove("has-error");
    });
}

// Ошибки сервера (422, 409) показываем под нужными полями
function showServerError(error) {
    if (error.details.length === 0) {
        errorBox.textContent = error.message;
        return;
    }

    error.details.forEach(function (detail) {
        if (FIELD_NAMES.indexOf(detail.field) === -1) {
            errorBox.textContent = detail.message;
            return;
        }

        document.querySelector("#error-" + detail.field).textContent = detail.message;
        document.querySelector("#" + detail.field).classList.add("has-error");
    });
}

async function onSubmit(event) {
    event.preventDefault();
    clearErrors();

    const data = {
        username: registerForm.elements.username.value,
        email: registerForm.elements.email.value,
        password: registerForm.elements.password.value
    };

    if (data.password !== registerForm.elements["password-repeat"].value) {
        document.querySelector("#error-password-repeat").textContent = "Пароли не совпадают";
        document.querySelector("#password-repeat").classList.add("has-error");
        return;
    }

    submitButton.disabled = true;

    try {
        await register(data);
        // После регистрации сразу входим тем же логином и паролем
        await login(data.username, data.password);
    } catch (error) {
        submitButton.disabled = false;
        showServerError(error);
        return;
    }

    window.location.href = "index.html";
}

function markSubmitted() {
    registerForm.classList.add("was-submitted");
}

registerForm.addEventListener("invalid", markSubmitted, true);
registerForm.addEventListener("submit", markSubmitted);
registerForm.addEventListener("submit", onSubmit);
