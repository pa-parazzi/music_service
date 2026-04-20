import {paginationState} from "../store/PaginationState.js";

export async function pageResponseOfAlbumCollection(jwt){
    const pageResponse = await fetch
    (`/api/collection/albums?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await pageResponse.json();
}

export async function pageResponseOfSoundCollection(jwt){
    const pageResponse = await fetch
    (`/api/collection/tracks?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await pageResponse.json();
}