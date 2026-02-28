const form = document.getElementById("registration-form");

const fieldMap = {
    username: "usernameError",
    password: "passwordError",
    email: "emailError",
    dateOfBirth: "dateOfBirthError",
    avatar: "avatarError"
};

const feedback = document.getElementById("feedback");
const errorMessage = document.getElementById("errorMessage");

function clearMessages() {
    Object.values(fieldMap).forEach(id => {
        const el = document.getElementById(id);
        el.textContent = "";
        el.style.display = "none";
    });

    feedback.textContent = "";
    feedback.style.display = "none";

    errorMessage.textContent = "";
    errorMessage.style.display = "none";
}

function showFieldError(field, messages) {
    const errorDiv = document.getElementById(fieldMap[field]);
    if (!errorDiv) return;

    const requiredMessage = messages.find(m => m === "Обязательное поле");
    errorDiv.textContent = requiredMessage ?? messages[0];
    errorDiv.style.display = "block";
}

function showGlobalError(message) {
    errorMessage.textContent = message;
    errorMessage.style.display = "block";
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessages();

    const userPayload = {
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value.trim(),
        email: document.getElementById("email").value.trim(),
        dateOfBirth: document.getElementById("date_of_birth").value.trim()
    };

    const avatarFile = document.getElementById("avatar").files[0];

    const formData = new FormData();
    formData.append(
        "user",
        new Blob([JSON.stringify(userPayload)], {type: "application/json"})
    );

    if (avatarFile) {
        formData.append("file", avatarFile);
    }

    try {
        const response = await fetch("/api/auth/registration", {
            method: "POST",
            credentials: "include",
            body: formData
        });

        if (!response.ok) {
            const error = await response.json();

            if (error.code === "VALIDATION_ERROR") {
                Object.entries(error.fieldsError).forEach(([field, messages]) => {
                    showFieldError(field, messages);
                });
                return;
            }

            showGlobalError(error.message ?? "Ошибка регистрации");
            return;
        }

        const data = await response.json();
        localStorage.setItem("jwt", data.accessToken);

        feedback.textContent = "Регистрация прошла успешно!";
        feedback.style.display = "block";

        setTimeout(() => {
            window.location.href = "/music/main.html";
        }, 500);

    } catch (e) {
        showGlobalError("Ошибка соединения с сервером");
    }
});
