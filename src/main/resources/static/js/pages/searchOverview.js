import {renderSearchOverviewLayout} from "../components/searchView.js";
import {
    getFoundAlbumsByFragmentPaged,
    getFoundArtistsByFragmentPaged,
    getFoundSoundsByFragmentPaged
} from "../api/searchApi.js";
import {renderArtists} from "../components/artistsView.js";
import {renderAlbumCards} from "../components/albumsView.js";
import {initPlayAlbumCardsDelegation} from "../module/albums.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {renderSounds} from "../components/soundsView.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {loadCss, unloadCss} from "../core/resources.js";
import {resetPaginationState} from "../utils/util.js";

export async function initSearchOverviewPage({fragment}){
    const contentSectionsCss = loadCss("/css/layout/content-sections.css");
    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");
    const artistCardCss = loadCss("/css/components/artist-card.css");
    const soundsCss = loadCss("/css/components/sounds.css");

    document.title = "Поиск";

    resetPaginationState();

    const appContainer = document.getElementById("app");

    renderSearchOverviewLayout(appContainer, fragment);

    const artistsContainer = appContainer.querySelector(".artists");
    const albumsContainer = appContainer.querySelector(".albums");
    const soundsContainer = appContainer.querySelector(".sounds");

    const notFoundContainer = appContainer.querySelector(".not-found");

    const artistsPageResponse = await getFoundArtistsByFragmentPaged(fragment);
    const artists = artistsPageResponse.content;

    const albumsPageResponse = await getFoundAlbumsByFragmentPaged(fragment);
    const albums = albumsPageResponse.content;

    const soundsPageResponse = await getFoundSoundsByFragmentPaged(fragment);
    const sounds = soundsPageResponse.content;

    if((artistsPageResponse.status === 204) &&
        (albumsPageResponse.status === 204) &&
        (soundsPageResponse.status === 204)) {
        notFoundContainer.textContent = "По запросу " + "\"" + fragment + "\""+ " ничего не найдено";
        return;
    }

    renderArtists(artistsContainer, artists);

    renderAlbumCards(albumsContainer, albums);
    const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumsContainer);

    const likedSoundsIds = await getLikedSoundsIds();

    renderSounds({container: soundsContainer, soundList: sounds, likedSoundsIds: likedSoundsIds});
    paginationStateOfSounds.sounds = sounds;
    const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

    return function cleanUp(){
        removePlayAlbumsDelegation?.();
        removeSoundsDelegation?.();
        unloadCss(contentSectionsCss);
        unloadCss(albumsCardCss);
        unloadCss(artistCardCss);
        unloadCss(soundsCss);
        appContainer.innerHTML = "";
    }
}