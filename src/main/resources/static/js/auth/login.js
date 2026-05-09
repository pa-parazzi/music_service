export async function login(payload){
    return await fetch("/api/auth/login", {
        method: "POST",
        credentials: "include",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(payload)
    });
}