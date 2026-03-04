import {getToken} from "../user/auth.js";

export async function loadSoundLikes(){
    const jwt = getToken();

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