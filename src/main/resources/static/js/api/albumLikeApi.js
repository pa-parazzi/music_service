export async function getAlbumLike(jwt, albumId){
    const likeStatusResponse = await fetch(`/api/album-like/is-liked/${albumId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await likeStatusResponse.json();
}