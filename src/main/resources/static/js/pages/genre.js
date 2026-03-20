import {initSidebar} from "../module/sidebar.js";
import {getGenres} from "../api/genreApi.js";
import {renderGenres} from "../components/genresView.js";

async function initGenrePage(){
    const genresContainer = document.getElementById("genres");

    const genresJson = await getGenres();
    const genres = genresJson.genres;
    renderGenres(genresContainer, genres);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenrePage();
});