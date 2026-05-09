import {apiFetch} from "../api/httpClient.js";
import {clearAuth} from "../auth/accessTokenStorage.js";

export async function loadUserProfile(userProfileContainer, authButtonsContainer){
    const response = await apiFetch('/api/user/profile', {
        method: "GET"
    });
    if (!response.ok) {
        clearAuth();
        userProfileContainer.style.display = "none";
        authButtonsContainer.style.display = "flex";
        console.warn("Сессия истекла или токен недействителен");
        return;
    }
    return await response.json();
}