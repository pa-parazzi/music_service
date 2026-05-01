import {initSearchForm} from "../module/search.js";
import {initSidebar} from "../module/sidebar.js";
import {initPlayer} from "../module/player.js";
import {getNewAlbumReleasesPaged} from "../api/albumApi.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {getGenres} from "../api/genreApi.js";
import {resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums} from "../store/paginationState.js";
import {renderGenresWithLimit} from "../components/genresView.js";

async function initMainPage(){
    initPlayer();
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const albumsContainer = document.querySelector(".album-rows");
    const genresContainer = document.querySelector(".genre-rows");

    resetPaginationState();
    paginationStateOfAlbums.size = 7;
    const pageResponseOfNewAlbumReleases = await getNewAlbumReleasesPaged();
    loadAlbumsPaged(pageResponseOfNewAlbumReleases, albumsContainer);
    initPlayAlbumCardsDelegation(albumsContainer);

    const genresResponse = await getGenres();
    const genres = genresResponse.genres;
    renderGenresWithLimit(genresContainer, genres, 7);
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initMainPage();
});