import {initSounds} from "../sound/initSounds.js";
import {audioListener} from "../audio/audioListener.js";
import {loadSoundLikes} from "../sound/loadSoundLikes.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

export async function loadTrackCollection(){
    const trackCollection = document.getElementById("track-collection");

    const likedSounds = await loadSoundLikes();

    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedSounds)
    });

    const collectionData = await trackCollectionResponse.json();
    const soundList = collectionData.soundList;

    await initSounds({
        trackListContainer: trackCollection,
        soundList: soundList,
        likedSounds: likedSounds
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);
}