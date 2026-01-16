/* --- централизованные утилиты и замок --- */
let refreshInProgress = null;

function clearAuth() {
    localStorage.removeItem("jwt");
}

/* --- безопасный парсер JSON --- */
async function safeParseJson(response) {
    const contentType = response.headers.get("Content-Type") || "";
    if (!response.ok) return null;
    if (response.status === 204) return null;
    if (!contentType.includes("application/json")) {
        try { return await response.json(); } catch (e) { return null; }
    }
    try {
        return await response.json();
    } catch (e) {
        console.warn("safeParseJson: no JSON body", e);
        return null;
    }
}

/* --- refreshAccessToken с "замком" --- */
async function refreshAccessToken() {
    if (refreshInProgress) return refreshInProgress;

    refreshInProgress = (async () => {
        try {
            const res = await fetch("/api/auth/refresh", {
                method: "POST",
                credentials: "include"
            });

            if (!res.ok) {
                clearAuth();
                return false;
            }

            const data = await res.json();
            if (!data?.accessToken) {
                clearAuth();
                return false;
            }

            localStorage.setItem("jwt", data.accessToken);
            return true;
        } catch (e) {
            clearAuth();
            return false;
        } finally {
            refreshInProgress = null;
        }
    })();

    return refreshInProgress;
}


/* --- Обёртка для запросов с авторизацией и auto-refresh --- */
async function apiFetch(url, options = {}) {
    if (refreshInProgress) await refreshInProgress;

    const headers = { ...(options.headers || {}) };
    const token = localStorage.getItem("jwt");

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

        const newToken = localStorage.getItem("jwt");
        if (!newToken) return response;

        headers.Authorization = "Bearer " + newToken;

        return fetch(url, {
            ...options,
            headers,
            credentials: "include"
        });
    }

    return response;
}



/* --- Загрузка профиля --- */
async function loadProfile() {

    const nav = document.getElementById("sign-in/up-buttons");
    const userInfoDiv = document.getElementById("userInfo");

    window.currentUser = null;

    try {
        const response = await apiFetch('/lk/profile', {
            method: "GET"
        });

        if (response.ok) {
            const user = await response.json();
            window.currentUser = user;

            nav.style.display = "none";
            userInfoDiv.innerHTML = `
                    <img src="${user.avatar?.url}" alt="avatar">
                    <span>${user.username}</span>
                    <button id="logoutBtn" class="logout-btn">Выйти</button>
                `;

            const logoutBtn = document.getElementById("logoutBtn");
            logoutBtn.addEventListener("click", logout);

            userInfoDiv.style.display = "block";
        } else {
            clearAuth();
            userInfoDiv.style.display = "none";
            nav.style.display = "flex";
            console.warn("Сессия истекла или токен недействителен");
        }
    } catch (error) {
        console.error("Ошибка загрузки профиля:", error);
        userInfoDiv.style.display = "none";
        nav.style.display = "flex";
    }

    return window.currentUser;
}

/* --- Logout --- */
async function logout() {
    try {
        const response = await apiFetch('/api/auth/logout', {
            method: "POST",
            credentials: "include"
        });

        clearAuth();
        if (response.ok) {
            console.log("Выход выполнен успешно");
            window.location.href = "/music/main.html";
        } else {
            console.warn("Ошибка при выходе:", response.status);
        }
    } catch (error) {
        console.error("Ошибка logout:", error);
    }
}

window.loadUser = loadProfile();