import {initSidebar} from "../module/sidebar.js";
import {getGenres} from "../api/genreApi.js";
import {renderGenresPage} from "../components/genresView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";

async function initGenrePage(){
    initPlayer();
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const mainContainer = document.getElementById("main-container");
    const genresContainer = mainContainer.querySelector(".genres");

    const genresJson = await getGenres();
    const genres = genresJson.genres;
    renderGenresPage(genresContainer, genres);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenrePage();
});