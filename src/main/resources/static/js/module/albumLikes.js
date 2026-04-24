export async function initAlbumLikeBtn(albumId, statusLikedAlbum, albumLikeBtn, jwt){
    if (statusLikedAlbum.likeStatus === true) {
        albumLikeBtn.classList.add("liked");
    }
    albumLikeBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (albumLikeBtn.classList.contains("liked")) {
            await fetch(`/api/album-like/${albumId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            albumLikeBtn.classList.toggle("liked", false);
        } else if (!albumLikeBtn.classList.contains("liked")) {
            await fetch(`/api/album-like/${albumId}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            albumLikeBtn.classList.toggle("liked", true);
        }
    });
}