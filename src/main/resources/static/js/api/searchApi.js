import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";

export async function getFoundSoundsByFragmentPaged(fragment){
    const response = await fetch
    (`/api/search/${fragment}/tracks?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundAlbumsByFragmentPaged(fragment){
    const response = await fetch
    (`/api/search/${fragment}/albums?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`, {
        method: "GET"
    });
    return await response.json();
}

export async function getFoundArtistsByFragmentPaged(fragment){
    const response = await fetch
    (`/api/search/${fragment}/artists?page=${paginationStateOfArtists.currentPage}&size=${paginationStateOfArtists.size}`, {
        method: "GET"
    });
    return await response.json();
}