//TODO: исправить запрос
export async function getAlbumCollection(jwt){
    const albumCollectionResponse = await fetch('/api/collection/albums', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedAlbums)
    });

    return await albumCollectionResponse.json();
}