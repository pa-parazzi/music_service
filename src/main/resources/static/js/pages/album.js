import {getToken} from "../user/auth.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundsByAlbumId} from "../api/soundApi.js";
import {getAlbumById} from "../api/albumApi.js";
import {renderAlbumPage} from "../components/albumView.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikeBtn} from "../module/albumLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {initPlayAlbumButton} from "../module/albums.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {playerState} from "../store/playerState.js";
import {resetPaginationState} from "../utils/util.js";
import {renderSounds} from "../components/soundsView.js";

async function initAlbumPage() {
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    resetPaginationState();

    const id = window.location.pathname.split('/').pop();

    const albumContainer = document.getElementById('album-container');
    const album = await getAlbumById(id);
    renderAlbumPage(albumContainer, album);

    const playAlbumBtn = document.querySelector(".play-album-btn");
    playerState.currentPlayAlbumButton = playAlbumBtn;
    initPlayAlbumButton(id, playAlbumBtn);

    const albumLikeBtn = document.querySelector(".album-like-btn");
    const statusLikedAlbum = await getAlbumLike(jwt, id);
    await initAlbumLikeBtn(id, statusLikedAlbum, albumLikeBtn, jwt);

    const trackListContainer = document.getElementById('tracklist');

    const likedSoundsResponse = await getLikedSoundsIds(jwt);
    const likedSoundsIds = new Set(likedSoundsResponse.ids);

    const soundsResponse = await getSoundsByAlbumId(id);
    const sounds = soundsResponse.sounds;
    paginationStateOfSounds.sounds = sounds;
    renderSounds({
        container: trackListContainer,
        soundList: sounds,
        likedSoundsIds: likedSoundsIds
    });
    initSoundsDelegation(trackListContainer, likedSoundsIds, jwt, id);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumPage();
});