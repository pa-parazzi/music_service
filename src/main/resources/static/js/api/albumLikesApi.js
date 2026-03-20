export async function getAlbumLikes(jwt){
    const likedAlbumsIdsResponse = await fetch('/api/liked-albums', {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });

    return await likedAlbumsIdsResponse.json();
}