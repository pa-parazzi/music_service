import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";

export async function getGenres(){
    const response = await fetch('/api/genre', {
        method: "GET"
    });
    if(!response.ok) throw new Error("Failed to load genres");
    return  await response.json();
}

export async function getGenreById(id){
    const response = await fetch(`/api/genre/${id}`, {
        method: "GET"
    });
    if(!response.ok) throw new Error("Failed to load genre");
    return  await response.json();
}

export async function getSoundsByGenreIdPaged(genreId){
    const response = await fetch
    (`/api/genre/${genreId}/tracks?page=${paginationStateOfSounds.currentPage}&size=${paginationStateOfSounds.size}`, {
        method: "GET"
    });
    if(!response.ok) throw new Error("Failed to load sounds");
    return await response.json();
}

export async function getAlbumsByGenreIdPaged(genreId){
    const response = await fetch
    (`/api/genre/${genreId}/albums?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`, {
        method: "GET"
    });
    if(!response.ok) throw new Error("Failed to load albums");
    return await response.json();
}