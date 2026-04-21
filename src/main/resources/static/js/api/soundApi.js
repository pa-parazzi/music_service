import {paginationState} from "../store/PaginationState.js";

export async function getSoundListByAlbumIdPaged(id){
    const pageResponse = await fetch
    (`/api/sound/album/${id}?page=${paginationState.currentPage}&size=${paginationState.size}`);
    return await pageResponse.json();
}

export async function getSoundListByArtistIdPaged(id){
    const pageResponse = await fetch
    (`/api/sound/artist/${id}?page=${paginationState.currentPage}&size=${paginationState.size}`);
    return await pageResponse.json();
}

export async function getSoundById(id){
    const soundResponse = await fetch(`/api/sound/${id}`);
    return await soundResponse.json();
}