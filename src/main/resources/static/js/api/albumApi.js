import {paginationStateOfAlbums} from "../store/paginationState.js";

export async function getAlbumById(id){
    const response = await fetch(`/api/album/${id}`);
    if (!response.ok) throw new Error("Failed to load album");
    return await response.json();
}

export async function getNewAlbumReleasesPaged(){
    const response = await fetch
    (`/api/album/releases?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`);
    if(!response.ok) throw new Error("Failed to load album releases");
    return await response.json();
}