import {login} from "../auth/login.js";
import {createFormErrorManager} from "../utils/formErrorManager.js";

export function initLoginForm(
    {
        form,
        usernameInput,
        passwordInput,
        usernameError,
        passwordError,
        errorMessage,
        successMessage
    }
){
    const errorManager = createFormErrorManager({
        errorMap: {
            username: usernameError,
            password: passwordError
        },
        errorMessage,
        successMessage
    });

    async function handleSubmit(event){
        event.preventDefault();
        errorManager.clear();

        const payload = {
            username: usernameInput.value.trim(),
            password: passwordInput.value.trim()
        };

        try {
            const response = await login(payload);

            const responseData = await response.json();

            if (!response.ok) {

                switch (responseData.code) {

                    case "VALIDATION_ERROR":
                        errorManager.showFieldsErrors(responseData.fieldsError);
                        break;

                    case "BAD_CREDENTIALS":
                    case "ACCOUNT_NOT_ACTIVATED":
                    case "ACCOUNT_LOCKED":
                    case "BAD_AUTHENTICATION_REQUEST":
                        errorManager.showGlobalError(responseData.message);
                        break;

                    default:
                        errorManager.showGlobalError("Произошла неизвестная ошибка");
                }
                return;
            }

            localStorage.setItem("jwt", responseData.accessToken);

            errorManager.showSuccess("Успешный вход!");

            setTimeout(() => {
                window.location.href = "/main";
            }, 1000);

        } catch (e) {
            errorManager.showGlobalError("Ошибка соединения с сервером");
        }
    }

    form.addEventListener("submit", handleSubmit);

    return function cleanUp() {
        form.removeEventListener("submit", handleSubmit);
    }
}