export async function getLikedSoundsIds(jwt){
    const likedSoundsIdsResponses = await fetch('/api/private/sound-like', {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });

    if(!likedSoundsIdsResponses.ok){
        return;
    }
    return await likedSoundsIdsResponses.json();
}

export async function getSoundLikeStatusBySoundId(jwt, soundId){
    const likeStatusResponse = await fetch(`/api/private/sound-like/is-liked/${soundId}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    return await likeStatusResponse.json();
}

export async function deleteSoundLike(jwt, soundId){
    await fetch(`/api/private/sound-like/${soundId}`, {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
}

export async function createSoundLike(jwt, soundId){
    await fetch(`/api/private/sound-like/${soundId}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
}