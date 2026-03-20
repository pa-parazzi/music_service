export async function getSoundCollection(likedSounds){
    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedSounds)
    });

    return await trackCollectionResponse.json();
}