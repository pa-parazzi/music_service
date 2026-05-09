import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {apiFetch} from "./httpClient.js";
import {forceLogout} from "../auth/logout.js";

export async function pageResponseOfAlbumCollection(){
    const response = await apiFetch
    (`/api/private/collection/albums?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`, {
        method: "GET"
    });
    if(response.status === 401){
        forceLogout();
    }
    if(!response.ok) throw new Error("Failed to load album collection");
    return await response.json();
}

export async function pageResponseOfSoundCollection(){
    const response = await apiFetch
    (`/api/private/collection/tracks?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`, {
        method: "GET"
    });
    if(response.status === 401){
        forceLogout();
    }
    if(!response.ok) throw new Error("Failed to load sound collection");
    return await response.json();
}