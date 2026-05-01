let refreshInProgress = null;

export function clearAuth() {
    localStorage.removeItem("jwt");
}

export function getToken() {
    return localStorage.getItem("jwt");
}

export function setToken(token) {
    localStorage.setItem("jwt", token);
}

export async function refreshAccessToken() {
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
            if (!data.accessToken) {
                clearAuth();
                return false;
            }

            setToken(data.accessToken);
            return true;
        } catch {
            clearAuth();
            return false;
        } finally {
            refreshInProgress = null;
        }
    })();

    return refreshInProgress;
}