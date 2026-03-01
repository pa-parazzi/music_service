import {initSoundListWithLikes} from "../sound/soundListWithLikes.js";
import {audioListener} from "../audio/audioListener.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

export async function loadTrackCollection(user){
    const trackCollection = document.getElementById("track-collection");

    if (!user) {
        console.log("Пользователь не авторизован");
        return;
    }
    const userId = user.id;

    const likedSoundsIdsResponses = await fetch('/api/liked-sounds/get', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    const likedSounds= await likedSoundsIdsResponses.json();

    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedSounds)
    });

    const collectionData = await trackCollectionResponse.json();
    const soundList = collectionData.soundList;

    await initSoundListWithLikes({
        trackListContainer: trackCollection,
        soundList: soundList,
        userId: userId
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);
}


