import {getToken} from "../user/refreshAccessToken.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getSoundsByAlbumId} from "../api/soundApi.js";
import {getAlbumById, getNewAlbumReleasesPaged} from "../api/albumApi.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikeBtn} from "../module/albumLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {initPlayAlbumButton, initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {playerState} from "../store/playerState.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {renderSounds} from "../components/soundsView.js";
import {router} from "../core/albumRouter.js";
import {renderAlbumPage, renderAlbumsContainer} from "../components/albumsView.js";

export async function initAlbumPage(id) {
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    resetPaginationState();

    const mainContainer = document.getElementById('main-container');
    const album = await getAlbumById(id);
    renderAlbumPage(mainContainer, album);

    const playAlbumBtn = document.querySelector(".album-page__play-btn");
    playerState.currentPlayAlbumButton = playAlbumBtn;
    initPlayAlbumButton(id, playAlbumBtn);

    const albumLikeBtn = document.querySelector(".album-like-btn");
    const statusLikedAlbum = await getAlbumLike(jwt, id);
    await initAlbumLikeBtn(id, statusLikedAlbum, albumLikeBtn, jwt);

    const soundsContainer = document.getElementById('sounds');

    const likedSoundsResponse = await getLikedSoundsIds(jwt);
    const likedSoundsIds = new Set(likedSoundsResponse.ids);

    const soundsResponse = await getSoundsByAlbumId(id);
    const sounds = soundsResponse.sounds;
    paginationStateOfSounds.sounds = sounds;
    renderSounds({
        container: soundsContainer,
        soundList: sounds,
        likedSoundsIds: likedSoundsIds
    });
    initSoundsDelegation(soundsContainer, likedSoundsIds, jwt, id);
}

export async function initAlbumReleasesPage(){
    initPlayer();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    resetPaginationState();
    paginationStateOfAlbums.size = 14;

    const mainContainer = document.getElementById('main-container');
    renderAlbumsContainer(mainContainer);

    const scrollAnchor = mainContainer.querySelector(".scroll-anchor");

    const albumsHeading = mainContainer.querySelector(".album-rows-heading");
    albumsHeading.textContent = "Новинки этого года";

    const albumsEl = mainContainer.querySelector(".album-rows");

    const pageResponse = await getNewAlbumReleasesPaged();
    loadAlbumsPaged(pageResponse, albumsEl);

    initPlayAlbumCardsDelegation(albumsEl);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await getNewAlbumReleasesPaged();
            loadAlbumsPaged(pageResponse, albumsEl);
        },
        hasNextFn: () => paginationStateOfAlbums.hasNext,
        isLoadingFn: () => paginationStateOfAlbums.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await router();
});