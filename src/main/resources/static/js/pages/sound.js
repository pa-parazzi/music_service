import {formatTime} from "../util.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundById} from "../api/soundApi.js";
import {renderSoundDetails} from "../components/soundView.js";
import {playSound} from "../module/playSound.js";
import {getSoundLikeStatusResponseBySoundId} from "../api/soundLikesApi.js";
import {initSoundLikeBySoundId} from "../module/soundLikes.js";

async function initSoundPage(){
    const jwt = getToken();
    const soundId = window.location.pathname.split('/').pop();

    const soundContainer = document.getElementById("sound");
    const player = document.getElementById("player");
    const playBtn = document.getElementById("play-btn");

    const sound = await getSoundById(soundId);
    const artist = sound.artist;
    const album = sound.album;
    const trackDuration = formatTime(sound.duration);

    renderSoundDetails(soundContainer, sound, trackDuration, artist, album);

    const playSoundBtn = document.querySelector('.play-sound-btn');
    const likeBtn = document.querySelector('.like-btn');

    playSound(playSoundBtn, player, playBtn, sound);

    const likeSoundStatus = await getSoundLikeStatusResponseBySoundId(soundId);

    await initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initSoundPage();
});