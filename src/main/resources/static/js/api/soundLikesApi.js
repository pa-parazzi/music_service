import {apiFetch} from "../user/api.js";

export async function getSoundLikes(jwt){
    const likedSoundsIdsResponses = await fetch('/api/liked-sounds', {
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

export async function getSoundLikeStatusResponseBySoundId(soundId){
    const likeStatusResponse = await apiFetch(`/api/liked-sounds/is-liked/${soundId}`);
    return await likeStatusResponse.json();
}