import {clearAuth, getToken, refreshAccessToken} from "./refreshAccessToken.js";

export async function loadUserProfile(userProfileContainer, authButtonsContainer){
    try {
        const response = await apiFetch('/api/user/profile', {
            method: "GET"
        });
        if(!response.ok){
            clearAuth();
            userProfileContainer.style.display = "none";
            authButtonsContainer.style.display = "flex";
            console.warn("Сессия истекла или токен недействителен");
            return;
        }
        return await response.json();
    } catch (e) {
        clearAuth();
        userProfileContainer.style.display = "none";
        authButtonsContainer.style.display = "flex";
        console.warn("Ошибка при загрузке данных пользователя: ", e);
    }
}

export async function apiFetch(url, options = {}) {
    const headers = { ...(options.headers || {}) };
    const token = getToken();

    if (token) {
        headers.Authorization = "Bearer " + token;
    }

    const response = await fetch(url, {
        ...options,
        headers,
        credentials: "include"
    });

    if ((response.status === 401 || response.status === 403) && token) {
        const refreshed = await refreshAccessToken();
        if (!refreshed) return response;

        headers.Authorization = "Bearer " + getToken();

        return fetch(url, {
            ...options,
            headers,
            credentials: "include"
        });
    }

    return response;
}