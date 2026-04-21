import {apiFetch} from "../user/api.js";
import {paginationState} from "../store/PaginationState.js";

export async function getGenres(){
    const response = await apiFetch('/api/genre', {
        method: "GET"
    });
    if (!response.ok){
        throw new Error("Ошибка загрузки списка жанров");
    }
    return  await response.json();
}

export async function getGenreById(id){
    const response = await apiFetch(`/api/genre/${id}`, {
        method: "GET"
    });
    if (!response.ok){
        throw new Error("Ошибка загрузки жанра id: " + id);
    }
    return  await response.json();
}

export async function getSoundsByGenreIdPaged(genreId){
    const pageResponse = await apiFetch
    (`/api/genre/${genreId}/tracks?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET"
    });
    return await pageResponse.json();
}

export async function getAlbumsByGenreIdPaged(genreId){
    const pageResponse = await apiFetch
    (`/api/genre/${genreId}/albums?page=${paginationState.currentPage}&size=${paginationState.size}`, {
        method: "GET"
    });
    return await pageResponse.json();
}