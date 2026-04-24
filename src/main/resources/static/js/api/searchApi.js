import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/PaginationState.js";

export async function getFoundSoundsByFragment(fragment){
    const response = await fetch
    (`/api/search/${fragment}/tracks?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundAlbumsByFragment(fragment){
    const response = await fetch
    (`/api/search/${fragment}/albums?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundArtistsByFragment(fragment){
    const response = await fetch
    (`/api/search/${fragment}/artists?page=${paginationStateOfArtists.currentPage}&size=${paginationStateOfArtists.size}`, {
        method: "GET"
    });
    return await response.json();
}