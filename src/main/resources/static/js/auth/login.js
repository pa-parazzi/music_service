const form = document.getElementById("loginForm");

const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");

const usernameError = document.getElementById("usernameError");
const passwordError = document.getElementById("passwordError");

const errorMessage = document.getElementById("errorMessage");
const feedback = document.getElementById("feedback");

function clearMessages() {
    [usernameError, passwordError, errorMessage, feedback].forEach(el => {
        el.textContent = "";
        el.style.display = "none";
    });
}

function showFieldError(field, messages) {
    // если есть "Обязательное поле" — показываем только его
    const requiredMessage = messages.find(m => m === "Обязательное поле");
    const messageToShow = requiredMessage ?? messages[0];

    const errorDiv = field === "username" ? usernameError : passwordError;
    errorDiv.textContent = messageToShow;
    errorDiv.style.display = "block";
}

function showGlobalError(message) {
    errorMessage.textContent = message;
    errorMessage.style.display = "block";
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessages();

    const payload = {
        username: usernameInput.value.trim(),
        password: passwordInput.value.trim()
    };

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            credentials: "include",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const error = await response.json();

            switch (error.code) {

                case "VALIDATION_ERROR":
                    if (error.fieldsError?.username) {
                        showFieldError("username", error.fieldsError.username);
                    }
                    if (error.fieldsError?.password) {
                        showFieldError("password", error.fieldsError.password);
                    }
                    break;

                case "BAD_CREDENTIALS":
                case "ACCOUNT_NOT_ACTIVATED":
                case "ACCOUNT_LOCKED":
                case "BAD_AUTHENTICATION_REQUEST":
                    showGlobalError(error.message);
                    break;

                default:
                    showGlobalError("Произошла неизвестная ошибка");
            }

            return;
        }

        const data = await response.json();
        localStorage.setItem("jwt", data.accessToken);

        feedback.textContent = "Успешный вход!";
        feedback.style.display = "block";

        setTimeout(() => {
            window.location.href = "/music/main.html";
        }, 1500);

    } catch (e) {
        showGlobalError("Ошибка соединения с сервером");
    }
});
