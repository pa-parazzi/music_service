import {registration} from "../auth/registration.js";
import {createFormErrorManager} from "../utils/formErrorManager.js";

export function initRegistrationForm(
    {
        form,
        usernameInput,
        passwordInput,
        emailInput,
        dateOfBirthInput,
        avatarInput,
        usernameError,
        passwordError,
        emailError,
        dateOfBirthError,
        avatarError,
        successMessage,
        errorMessage
    }
){
    const errorManager = createFormErrorManager({
        errorMap: {
            username: usernameError,
            password: passwordError,
            email: emailError,
            dateOfBirth: dateOfBirthError,
            avatar: avatarError
        },
        errorMessage,
        successMessage
    });

    async function handleSubmit(event){
        event.preventDefault();
        errorManager.clear();

        const payload = {
            username: usernameInput.value.trim(),
            password: passwordInput.value.trim(),
            email: emailInput.value.trim(),
            dateOfBirth: dateOfBirthInput.value.trim()
        };

        const avatarFile = avatarInput.files[0];

        const formData = new FormData();
        formData.append(
            "user",
            new Blob([JSON.stringify(payload)], {type: "application/json"})
        );

        if (avatarFile) {
            formData.append("file", avatarFile);
        }

        try {
            const response = await registration(formData);
            const responseData = await response.json();

            if (!response.ok) {
                if (responseData.code === "VALIDATION_ERROR") {
                    errorManager.showFieldsErrors(responseData.fieldsError)
                    return;
                }

                errorManager.showGlobalError(responseData.message);
                return;
            }

            localStorage.setItem("jwt", responseData.accessToken);

            errorManager.showSuccess("Регистрация прошла успешно!");

            setTimeout(() => {
                window.location.href = "/main";
            }, 500);

        } catch (e) {
            errorManager.showGlobalError("Ошибка соединения с сервером");
        }
    }

    form.addEventListener("submit", handleSubmit);

    return function cleanUp() {
        form.removeEventListener("submit", handleSubmit);
    }
}