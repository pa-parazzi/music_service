export async function getAlbumLike(jwt, albumId){
    const likeStatusResponse = await fetch(`/api/private/album-like/is-liked/${albumId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await likeStatusResponse.json();
}

export async function deleteAlbumLike(jwt, albumId){
    await fetch(`/api/private/album-like/${albumId}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
}

export async function createAlbumLike(jwt, albumId){
    await fetch(`/api/private/album-like/${albumId}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
}