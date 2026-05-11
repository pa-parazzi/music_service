import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {getSoundsByAlbumId} from "../api/soundApi.js";
import {getAlbumById} from "../api/albumApi.js";
import {getAlbumLike} from "../api/albumLikeApi.js";
import {initAlbumLikeBtn} from "../module/albumLikes.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {initPlayAlbumButton} from "../module/albums.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {resetPaginationState} from "../utils/util.js";
import {loadCss, unloadCss} from "../core/resources.js";
import {renderSounds} from "../components/soundsView.js";
import {renderAlbumPage} from "../components/albumsView.js";

export async function initAlbumPage({id}) {
    const albumCss = loadCss("/css/pages/album.css");
    const soundsCss = loadCss("/css/components/sounds.css");

    const albumId = Number(id);

    resetPaginationState();

    const appContainer = document.getElementById("app");

    const album = await getAlbumById(albumId);

    document.title = "Альбом: " + album.title;

    const albumPageContainer = renderAlbumPage(appContainer, album);

    const playAlbumBtn = albumPageContainer.querySelector(".album-page__play-btn");
    const albumLikeBtn = albumPageContainer.querySelector(".album-like-btn");
    const soundsContainer = albumPageContainer.querySelector(".sounds");

    const removePlayAlbumDelegation = initPlayAlbumButton(albumId, playAlbumBtn);

    const soundsResponse = await getSoundsByAlbumId(albumId);
    const sounds = soundsResponse.sounds;
    paginationStateOfSounds.sounds = sounds;

    const statusLikedAlbum = await getAlbumLike(albumId);
    const removeAlbumLikeDelegation = initAlbumLikeBtn(albumId, statusLikedAlbum, albumLikeBtn);

    const likedSoundsIds = await getLikedSoundsIds();

    renderSounds({
        container: soundsContainer,
        soundList: sounds,
        likedSoundsIds: likedSoundsIds
    });

    const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

    return function cleanUp() {
        removePlayAlbumDelegation?.();
        removeAlbumLikeDelegation?.();
        removeSoundsDelegation?.();
        unloadCss(albumCss);
        unloadCss(soundsCss);
        appContainer.innerHTML = "";
    }
}