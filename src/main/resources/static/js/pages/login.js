import {loadLoginForm} from "../components/loginForm.js";
import {initLoginForm} from "../module/login.js";
import {loadCss, unloadCss} from "../core/resources.js";

export function initLoginPage(){

    const loginFormCss = loadCss("/css/auth/auth-form.css");

    document.title = "Вход";

    const appContainer = document.getElementById("app");
    loadLoginForm(appContainer);

    const form = appContainer.querySelector(".login-form");

    const usernameInput = form.querySelector(".username");
    const passwordInput = form.querySelector(".password");

    const usernameError = form.querySelector(".username-error");
    const passwordError = form.querySelector(".password-error");

    const errorMessage = form.querySelector(".error-message");
    const successMessage = form.querySelector(".success-message");

    const cleanUpLoginForm = initLoginForm({
        form: form,
        usernameInput: usernameInput,
        passwordInput: passwordInput,
        usernameError: usernameError,
        passwordError: passwordError,
        errorMessage: errorMessage,
        successMessage: successMessage
    });

    return function cleanupPage() {
        cleanUpLoginForm();
        unloadCss(loginFormCss);
        appContainer.innerHTML = "";
    };
}