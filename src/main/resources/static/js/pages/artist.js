import{audioListener} from "../audio/audio-listener.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getArtistById} from "../api/artistApi.js";
import {getSoundListByArtistId} from "../api/soundApi.js";
import {getToken} from "../user/auth.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";

async function initArtistPage() {
    const jwt = getToken();
    const id = window.location.pathname.split('/').pop();
    const artist = await getArtistById(id);

    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

    const artistName = document.getElementById('artist-name');
    artistName.textContent = artist.name;

    const tracksContainer = document.getElementById('tracklist');
    const soundList = await getSoundListByArtistId(id);
    renderSounds(tracksContainer, soundList);

    const likedSounds = await getSoundLikes(jwt);
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
    await initArtistPage();
});
