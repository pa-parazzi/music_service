import {audioListener} from "../audio/audio-listener.js";
import {getToken} from "../user/auth.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {playAlbum} from "../module/playAlbum.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";
import {getAlbumById} from "../api/albumApi.js";
import {renderAlbum} from "../components/albumView.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikes} from "../module/albumLikes.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";

async function initAlbumPage() {
    const id = window.location.pathname.split('/').pop();

    const album = await getAlbumById(id);
    const soundList = await getSoundListByAlbumId(id);

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const albumContainer = document.getElementById('album-container');
    renderAlbum(albumContainer, album);

    const trackListContainer = document.getElementById('tracklist');
    renderSounds(trackListContainer, soundList);

    const playAlbumBtn = document.querySelector(".play-album-btn");
    const albumLikeBtn = document.querySelector(".album-like-btn");
    const soundLikeButtons = document.querySelectorAll(".like-btn");

    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

    const jwt = getToken();
    const likedSounds = await getSoundLikes(jwt);

    await initSoundLikes(likedSounds, soundLikeButtons, jwt);

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);

    playAlbum(player, playBtn, playAlbumBtn, playerState, soundList);

    const statusLikedAlbum = await getAlbumLike(id, jwt);

    await initAlbumLikes(id, statusLikedAlbum, albumLikeBtn, jwt);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumPage();
});