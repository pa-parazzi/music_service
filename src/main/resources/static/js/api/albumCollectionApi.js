export async function getAlbumCollection(likedAlbums){
    const albumCollectionResponse = await fetch('/collection/albums', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedAlbums)
    });

    return await albumCollectionResponse.json();
}