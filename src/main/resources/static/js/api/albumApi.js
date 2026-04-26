import {paginationStateOfAlbums} from "../store/paginationState.js";

export async function getAlbumById(id){
    const response = await fetch(`/api/album/${id}`);
    if (!response.ok) throw new Error("Ошибка загрузки альбома");
    return await response.json();
}

export async function getNewAlbumReleasesPaged(){
    const pageResponse = await fetch
    (`/api/album/releases?page=${paginationStateOfAlbums.currentPage}&size=${paginationStateOfAlbums.size}`);
    return await pageResponse.json();
}