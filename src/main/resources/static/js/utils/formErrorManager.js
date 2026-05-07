export function createFormErrorManager(
    {
        errorMap, errorMessage, successMessage
    }
) {

    function clear() {
        Object.values(errorMap).forEach(el => {
            el.textContent = "";
            el.style.display = "none";
        });

        [errorMessage, successMessage].forEach(el => {
            el.textContent = "";
            el.style.display = "none";
        });
    }

    function showFieldError(field, messages) {
        const errorDiv = errorMap[field];
        if (!errorDiv) return;

        const requiredMessage = messages.find(m => m === "Обязательное поле");
        errorDiv.textContent = requiredMessage ?? messages[0];
        errorDiv.style.display = "block";
    }

    function showFieldsErrors(fieldsError) {
        Object.entries(fieldsError).forEach(([field, messages]) => {
            showFieldError(field, messages);
        });
    }

    function showGlobalError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = "block";
    }

    function showSuccess(message) {
        successMessage.textContent = message;
        successMessage.style.display = "block";
    }

    return {
        clear,
        showFieldsErrors,
        showGlobalError,
        showSuccess
    };
}