import {apiFetch} from "../user/api.js";

export async function getSoundLikes(jwt){
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

export async function getSoundLikeStatusBySoundId(soundId){
    const likeStatusResponse = await apiFetch(`/api/sound-like/is-liked/${soundId}`);
    return await likeStatusResponse.json();
}