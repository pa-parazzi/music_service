import {loadAdminLayout} from "./adminView.js";
import {getGenres} from "../api/genreApi.js";
import {initImportBtn} from "./importButton.js";

export async function initAdminPage() {
    const appContainer = document.getElementById("app");
    loadAdminLayout(appContainer);

    const importBtn = appContainer.querySelector(".import-button");
    const importStatus = appContainer.querySelector(".import-status");
    const genreSelect = appContainer.querySelector(".genre-select");

    const genresJson = await getGenres();
    const genres = genresJson.genres;

    genreSelect.innerHTML = "";
    genres.forEach(genre => {
        const option = document.createElement('option');
        option.value = genre.name;
        option.textContent = genre.name;
        genreSelect.appendChild(option);
    });

    const removeImportBtnDelegation = initImportBtn(importBtn, importStatus, genreSelect);

    return function cleanUp(){
        removeImportBtnDelegation?.();
        appContainer.innerHTML = "";
    }
}