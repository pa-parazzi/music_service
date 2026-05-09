import {refreshAccessToken} from "../auth/refreshAccessToken.js";
import {clearAuth, getToken} from "../auth/accessTokenStorage.js";

let refreshPromise = null;

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

    if (response.status !== 401) {
        return response;
    }

    if (!token) {
        clearAuth();
        return response;
    }

    if (!refreshPromise) {
        refreshPromise = refreshAccessToken()
            .finally(() => refreshPromise = null);
    }

    const refreshed = await refreshPromise;

    if (!refreshed) {
        clearAuth();
        return response;
    }

    const retryResponse = await fetch(url, {
        ...options,
        headers: {
            ...headers,
            Authorization: "Bearer " + getToken()
        },
        credentials: "include"
    });

    if (retryResponse.status === 401) {
        clearAuth();
    }

    return retryResponse;
}