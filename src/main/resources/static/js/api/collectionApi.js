import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";

export async function pageResponseOfAlbumCollection(jwt){
    const pageResponse = await fetch
    (`/api/collection/albums?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await pageResponse.json();
}

export async function pageResponseOfSoundCollection(jwt){
    const pageResponse = await fetch
    (`/api/collection/tracks?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await pageResponse.json();
}