import {apiFetch} from "../api/httpClient.js";

export async function loadUserProfile(){
    const response = await apiFetch('/api/user/profile', {
        method: "GET"
    });
    if(response.status === 401) {
        return null;
    }
    if(!response.ok) throw new Error("Failed to load user profile");
    return await response.json();
}