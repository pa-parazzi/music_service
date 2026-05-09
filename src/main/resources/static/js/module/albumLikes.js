import {createAlbumLike, deleteAlbumLike} from "../api/albumLikeApi.js";

export function initAlbumLikeBtn(albumId, albumLikeStatus, albumLikeBtn){
    if (albumLikeStatus && albumLikeStatus.likeStatus === true) {
        albumLikeBtn.classList.add("liked");
    }
    const albumLikeHandler = async (e) => {
        e.stopPropagation();
        if (albumLikeBtn.classList.contains("liked")) {
            await deleteAlbumLike(albumId);
            albumLikeBtn.classList.toggle("liked", false);
        } else if (!albumLikeBtn.classList.contains("liked")) {
            await createAlbumLike(albumId);
            albumLikeBtn.classList.toggle("liked", true);
        }
    };
    albumLikeBtn.addEventListener("click", albumLikeHandler);
    return function remove(){
        albumLikeBtn.removeEventListener("click", albumLikeHandler);
    }
}