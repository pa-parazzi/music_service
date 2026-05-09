import {apiFetch} from "./httpClient.js";
import {forceLogout} from "../auth/logout.js";

export async function getAlbumLike(albumId){
    const response = await apiFetch(`/api/private/album-like/is-liked/${albumId}`, {
        method: "GET"
    });
    if(response.status === 401) {
        return null;
    }
    if(!response.ok) throw new Error("Failed to load album like");
    return await response.json();
}

export async function deleteAlbumLike(albumId){
    const response = await apiFetch(`/api/private/album-like/${albumId}`, {
        method: "DELETE"
    });
    if(response.status === 401){
        forceLogout();
    }
}

export async function createAlbumLike(albumId){
    const response = await apiFetch(`/api/private/album-like/${albumId}`, {
        method: "POST"
    });
    if(response.status === 401){
        forceLogout();
    }
}