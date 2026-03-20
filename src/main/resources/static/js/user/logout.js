import {apiFetch} from "./api.js";
import {clearAuth} from "./auth.js";

export async function logout() {
    try {
        const response = await apiFetch('/api/auth/logout', {
            method: "POST",
            credentials: "include"
        });

        clearAuth();
        if (response.ok) {
            console.log("Выход выполнен успешно");
            window.location.href = "/main";
        } else {
            console.warn("Ошибка при выходе:", response.status);
        }
    } catch (error) {
        console.error("Ошибка logout:", error);
    }
}