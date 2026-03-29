import {formatTime} from "../utils/util.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundById} from "../api/soundApi.js";
import {renderSoundDetails} from "../components/soundView.js";
import {getSoundLikeStatusResponseBySoundId} from "../api/soundLikesApi.js";
import {initSoundLikeBySoundId} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initPlaySoundButton} from "../module/tracks.js";
import {playerState} from "../store/playerState.js";

async function initSoundPage(){
    initPlayer();
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
    playerState.currentPlaySoundButton = playSoundBtn;
    initPlaySoundButton(soundId, sound, playSoundBtn);

    const likeBtn = document.querySelector('.like-btn');
    const likeSoundStatus = await getSoundLikeStatusResponseBySoundId(soundId);
    await initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initSoundPage();
});