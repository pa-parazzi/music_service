import {paginationStateOfSounds} from "../store/PaginationState.js";

export async function getSoundsByAlbumId(id){
    const soundsResponse = await fetch(`/api/sound/album/${id}`);
    return await soundsResponse.json();
}

export async function getSoundsByArtistIdPaged(id){
    const pageResponse = await fetch
    (`/api/sound/artist/${id}?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`);
    return await pageResponse.json();
}

export async function getSoundById(id){
    const soundResponse = await fetch(`/api/sound/${id}`);
    return await soundResponse.json();
}