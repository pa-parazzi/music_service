import {getToken} from "../user/auth.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";
import {getAlbumById} from "../api/albumApi.js";
import {renderAlbum} from "../components/albumView.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikes} from "../module/albumLikes.js";
import {renderSounds} from "../components/soundsView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initTracksDelegation} from "../module/tracks.js";
import {initPlayAlbumButton} from "../module/albums.js";
import {paginationState} from "../store/PaginationState.js";
import {playerState} from "../store/playerState.js";

async function initAlbumPage() {
    initPlayer();
    const jwt = getToken();

    const id = window.location.pathname.split('/').pop();

    const album = await getAlbumById(id);
    const soundList = await getSoundListByAlbumId(id);
    paginationState.tracks = soundList;

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const albumContainer = document.getElementById('album-container');
    renderAlbum(albumContainer, album);
    const playAlbumBtn = document.querySelector(".play-album-btn");
    playerState.currentPlayAlbumButton = playAlbumBtn;
    initPlayAlbumButton(id, playAlbumBtn);

    const albumLikeBtn = document.querySelector(".album-like-btn");
    const statusLikedAlbum = await getAlbumLike(id, jwt);
    await initAlbumLikes(id, statusLikedAlbum, albumLikeBtn, jwt);

    const trackListContainer = document.getElementById('tracklist');
    const soundLikes = await getSoundLikes(jwt);
    const likedSoundsIds = new Set(soundLikes.ids);
    renderSounds({container: trackListContainer, soundList: soundList, likedSoundsIds: likedSoundsIds});
    initTracksDelegation(trackListContainer, likedSoundsIds, jwt, id);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumPage();
});