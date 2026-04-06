export async function getAlbumLikes(jwt){
    const likedAlbumsIdsResponse = await fetch('/api/album-like', {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });

    return await likedAlbumsIdsResponse.json();
}