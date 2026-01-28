import {initSoundListWithLikes} from "../soundListWithLikes.js";
import {audioListener} from "../audio/audioListener.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

async function loadTrackCollection(){
    const trackCollection = document.getElementById("track-collection");

    const userId = window.currentUser.id;

    const likeResponses = await fetch('/sound/like/get', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    const likeList = await likeResponses.json();

    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likeList)
    });

    const collectionData = await trackCollectionResponse.json();
    const soundList = collectionData.soundList;

    await initSoundListWithLikes({
        trackList: trackCollection,
        soundList: soundList
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadTrackCollection();
})();


