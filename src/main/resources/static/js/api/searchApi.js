import {paginationState} from "../store/PaginationState.js";

export async function getFoundTracksByFragment(fragment){
    const response = await fetch(`/api/search/${fragment}/tracks?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundAlbumsByFragment(fragment){
    const response = await fetch(`/api/search/${fragment}/albums?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundArtistsByFragment(fragment){
    const response = await fetch(`/api/search/${fragment}/artists?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET"
    });
    return await response.json();
}