export function clearAuth() {
    localStorage.removeItem("jwt");
}

export function getToken() {
    return localStorage.getItem("jwt");
}

export function setToken(token) {
    localStorage.setItem("jwt", token);
}