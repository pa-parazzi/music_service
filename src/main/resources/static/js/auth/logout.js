import {clearAuth} from "./accessTokenStorage.js";

export async function logout() {
    try {
        await fetch('/api/auth/logout', {
            method: "POST",
            credentials: "include"
        });
    } catch (error) {
        console.error("Ошибка logout:", error);
    }
    forceLogout();
}

export function forceLogout(){
    clearAuth();
    window.location.href = "/auth/login";
}