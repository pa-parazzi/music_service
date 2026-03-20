import {getToken} from "../user/auth.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";
import {getAlbumById} from "../api/albumApi.js";
import {renderAlbum} from "../components/albumView.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikes} from "../module/albumLikes.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {playerState} from "../store/playerState.js";

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

    const trackCards = document.querySelectorAll(".track-card");
    const playAlbumBtn = document.querySelector(".play-album-btn");
    const albumLikeBtn = document.querySelector(".album-like-btn");
    const soundLikeButtons = document.querySelectorAll(".like-btn");

    playerState.soundList = soundList;
    initPlayer({playAlbumBtn, trackCards});

    const jwt = getToken();
    const likedSounds = await getSoundLikes(jwt);

    await initSoundLikes(likedSounds, soundLikeButtons, jwt);

    const statusLikedAlbum = await getAlbumLike(id, jwt);

    await initAlbumLikes(id, statusLikedAlbum, albumLikeBtn, jwt);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumPage();
});