export async function getLikedSoundsIds(jwt){
    const likedSoundsIdsResponses = await fetch('/api/sound-like', {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });

    if(!likedSoundsIdsResponses.ok){
        console.log("Ошибка загрузки лайков для треков");
        return;
    }
    return await likedSoundsIdsResponses.json();
}

export async function getSoundLikeStatusBySoundId(jwt, soundId){
    const likeStatusResponse = await fetch(`/api/sound-like/is-liked/${soundId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await likeStatusResponse.json();
}