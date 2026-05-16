import {getNewAlbumReleasesPaged} from "../api/albumApi.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {getGenres} from "../api/genreApi.js";
import {resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums} from "../store/paginationState.js";
import {renderGenresWithLimit} from "../components/genresView.js";
import {renderMainPageLayout} from "../components/mainView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initMainPage(){
    const mainPageCss = loadCss("/css/pages/main.css");
    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");
    const genresCardCss = loadCss("/css/components/genres-card-rows.css");
    const contentSectionsCss = loadCss("/css/layout/content-sections.css");

    document.title = "Главная";

    const appContainer = document.getElementById("app");
    renderMainPageLayout(appContainer);

    const albumsContainer = appContainer.querySelector(".album-rows");
    const genresContainer = appContainer.querySelector(".genre-rows");

    resetPaginationState();
    paginationStateOfAlbums.size = 7;
    const pageResponseOfNewAlbumReleases = await getNewAlbumReleasesPaged();
    loadAlbumsPaged(pageResponseOfNewAlbumReleases, albumsContainer);
    const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumsContainer);

    const genresResponse = await getGenres();
    const genres = genresResponse.genres;
    renderGenresWithLimit(genresContainer, genres, 7);

    return function cleanUp(){
        removePlayAlbumsDelegation?.();
        unloadCss(mainPageCss);
        unloadCss(contentSectionsCss);
        unloadCss(albumsCardCss);
        unloadCss(genresCardCss);
        appContainer.innerHTML = "";
    }
}