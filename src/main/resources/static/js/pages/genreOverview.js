import {resetPaginationState} from "../utils/util.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {renderGenreContent, renderGenrePageContainer} from "../components/genresView.js";
import {getAlbumsByGenreIdPaged, getSoundsByGenreIdPaged} from "../api/genreApi.js";
import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {renderAlbumCards} from "../components/albumsView.js";
import {initPlayAlbumCardsDelegation} from "../module/albums.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initGenreOverviewPage({id}){
    const contentSectionsCss = loadCss("/css/layout/content-sections.css");
    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");
    const soundsCss = loadCss("/css/components/sounds.css");

    resetPaginationState();

    const genreId = Number(id);

    document.title = "Жанр";

    const appContainer = document.getElementById("app");
    const genrePageContainer = renderGenrePageContainer(appContainer);

    const pageSectionContainer = renderGenreContent(genrePageContainer, genreId);

    const soundsContainer = pageSectionContainer.querySelector(".sounds");
    const albumContainer = pageSectionContainer.querySelector(".albums");

    const likedSoundsIds = await getLikedSoundsIds();

    const pageResponse = await getSoundsByGenreIdPaged(genreId);
    const sounds = pageResponse.content;
    paginationStateOfSounds.sounds = sounds;

    renderSounds({
        container: soundsContainer,
        soundList: sounds,
        likedSoundsIds: likedSoundsIds
    });

    const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

    const albumsResponse = await getAlbumsByGenreIdPaged(genreId);
    const albums = albumsResponse.content;
    paginationStateOfAlbums.albums = albums;

    renderAlbumCards(albumContainer, albums);
    const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumContainer);

    return function cleanUp(){
        removeSoundsDelegation?.();
        removePlayAlbumsDelegation?.();
        unloadCss(contentSectionsCss);
        unloadCss(albumsCardCss);
        unloadCss(soundsCss);
        appContainer.innerHTML = "";
    }
}