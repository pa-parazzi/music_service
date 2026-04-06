export async function getAlbumLike(id, jwt){
    const likedAlbumStatusResponse = await fetch(`/api/album-like/is-liked/${id}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await likedAlbumStatusResponse.json();
}