import {initSidebar} from "../module/sidebar.js";
import {getGenres} from "../api/genreApi.js";
import {renderGenres} from "../components/genresView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";

async function initGenrePage(){
    initPlayer();
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const genresContainer = document.getElementById("genres");

    const genresJson = await getGenres();
    const genres = genresJson.genres;
    renderGenres(genresContainer, genres);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenrePage();
});