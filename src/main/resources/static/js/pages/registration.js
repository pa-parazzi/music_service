import {loadRegistrationForm} from "../components/registrationForm.js";
import {initRegistrationForm} from "../module/registration.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initRegistrationPage(){

    const authFormCss = loadCss("/css/auth/auth-form.css");

    document.title = "Регистрация";

    const appContainer = document.getElementById("app");
    loadRegistrationForm(appContainer);

    const form = appContainer.querySelector(".registration-form");

    const usernameInput = form.querySelector(".username");
    const passwordInput = form.querySelector(".password");
    const emailInput = form.querySelector(".email");
    const dateOfBirthInput = form.querySelector(".date-of-birth");
    const avatarInput = form.querySelector(".avatar");

    const usernameError = form.querySelector(".username-error");
    const passwordError = form.querySelector(".password-error");
    const emailError = form.querySelector(".email-error");
    const dateOfBirthError = form.querySelector(".date-of-birth-error");
    const avatarError = form.querySelector(".avatar-error");

    const successMessage = form.querySelector(".success-message");
    const errorMessage = form.querySelector(".error-message");

    const cleanUpRegistrationForm = initRegistrationForm(
        {
            form: form,
            usernameInput: usernameInput,
            passwordInput: passwordInput,
            emailInput: emailInput,
            dateOfBirthInput: dateOfBirthInput,
            avatarInput: avatarInput,
            usernameError: usernameError,
            passwordError: passwordError,
            emailError: emailError,
            dateOfBirthError: dateOfBirthError,
            avatarError: avatarError,
            successMessage: successMessage,
            errorMessage: errorMessage
    });

    return function cleanupPage() {
        cleanUpRegistrationForm();
        unloadCss(authFormCss);
        appContainer.innerHTML = "";
    };
}