import {formatTime} from "../utils/util.js";
import {getToken} from "../user/refreshAccessToken.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundById} from "../api/soundApi.js";
import {getSoundLikeStatusBySoundId} from "../api/soundLikesApi.js";
import {initSoundLikeBySoundId} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initPlaySoundButton} from "../module/sounds.js";
import {playerState} from "../store/playerState.js";
import {renderSoundPage} from "../components/soundsView.js";

async function initSoundPage(){
    initPlayer();
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();
    const soundId = window.location.pathname.split('/').pop();

    const mainContainer = document.getElementById("main-container");

    const sound = await getSoundById(soundId);
    const artist = sound.artist;
    const album = sound.album;
    const trackDuration = formatTime(sound.duration);

    renderSoundPage(mainContainer, sound, trackDuration, artist, album);

    const playSoundBtn = mainContainer.querySelector(".play-sound-btn");
    playerState.currentPlaySoundButton = playSoundBtn;
    initPlaySoundButton(soundId, sound, playSoundBtn);

    const likeBtn = mainContainer.querySelector(".like-btn");
    const likeSoundStatus = await getSoundLikeStatusBySoundId(jwt, soundId);
    await initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initSoundPage();
});