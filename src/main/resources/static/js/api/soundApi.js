import {paginationStateOfSounds} from "../store/paginationState.js";

export async function getSoundsByAlbumId(id){
    const response = await fetch(`/api/sound/album/${id}`);
    if(!response.ok) throw new Error("Failed to load sounds");
    return await response.json();
}

export async function getSoundsByArtistIdPaged(id){
    const response = await fetch
    (`/api/sound/artist/${id}?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`);
    if(!response.ok) throw new Error("Failed to load sounds");
    return await response.json();
}

export async function getSoundById(id){
    const response = await fetch(`/api/sound/${id}`);
    if(!response.ok) throw new Error("Failed to load sound");
    return await response.json();
}