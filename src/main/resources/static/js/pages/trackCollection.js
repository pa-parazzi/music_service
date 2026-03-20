import {audioListener} from "../audio/audio-listener.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getToken} from "../user/auth.js";
import {getSoundCollection} from "../api/soundCollectionApi.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";

export async function initTrackCollectionPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();
    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

    const trackCollection = document.getElementById("track-collection");

    const likedSounds = await getSoundLikes(jwt);

    const collectionData = await getSoundCollection(likedSounds);
    const soundList = collectionData.soundList;

    renderSounds(trackCollection, soundList);
    const soundLikeButtons = document.querySelectorAll('.like-btn');
    await initSoundLikes(likedSounds, soundLikeButtons, jwt);

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);
}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initTrackCollectionPage();
});