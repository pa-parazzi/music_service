import { getToken, refreshAccessToken } from "./auth.js";

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