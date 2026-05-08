import {getGenres} from "../api/genreApi.js";
import {renderGenresContainer, renderGenresPage} from "../components/genresView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initGenrePage(){
    document.title = "Жанры";
    const genrePageCss = loadCss("/css/pages/genre.css");

    const appContainer = document.getElementById("app");
    const genresContainer = renderGenresContainer(appContainer);

    const genresJson = await getGenres();
    const genres = genresJson.genres;
    renderGenresPage(genresContainer, genres);

    return function cleanUp(){
        unloadCss(genrePageCss);
        appContainer.innerHTML = "";
    }
}