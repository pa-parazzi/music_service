import {apiFetch} from "./httpClient.js";
import {forceLogout} from "../auth/logout.js";

export async function getLikedSoundsIds(){
    const response = await apiFetch('/api/private/sound-like', {
        method: "GET"
    });
    if(response.status === 401){
        return new Set();
    }
    const data = await response.json();
    return new Set(data.ids);
}

export async function getSoundLikeStatusBySoundId(soundId){
    const response = await apiFetch(`/api/private/sound-like/is-liked/${soundId}`, {
        method: "GET"
    });
    if(response.status === 401) {
        return null;
    }
    if(!response.ok) throw new Error("Failed to load sound like status");
    return await response.json();
}

export async function deleteSoundLike(soundId){
    const response = await apiFetch(`/api/private/sound-like/${soundId}`, {
        method: "DELETE"
    });
    if(response.status === 401){
        forceLogout();
    }
}

export async function createSoundLike(soundId){
    const response = await apiFetch(`/api/private/sound-like/${soundId}`, {
        method: "POST"
    });
    if(response.status === 401){
        forceLogout();
    }
}