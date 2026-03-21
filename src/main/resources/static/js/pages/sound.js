import {formatTime} from "../utils/util.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundById} from "../api/soundApi.js";
import {renderSoundDetails} from "../components/soundView.js";
import {getSoundLikeStatusResponseBySoundId} from "../api/soundLikesApi.js";
import {initSoundLikeBySoundId} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";

async function initSoundPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();
    const soundId = window.location.pathname.split('/').pop();

    const soundContainer = document.getElementById("sound");

    const sound = await getSoundById(soundId);
    const artist = sound.artist;
    const album = sound.album;
    const trackDuration = formatTime(sound.duration);

    renderSoundDetails(soundContainer, sound, trackDuration, artist, album);

    const playSoundBtn = document.querySelector('.play-sound-btn');
    const likeBtn = document.querySelector('.like-btn');

    await initPlayer({tracks: [sound], playSoundBtn: playSoundBtn});

    const likeSoundStatus = await getSoundLikeStatusResponseBySoundId(soundId);

    await initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initSoundPage();
});