import {apiFetch} from "../user/api.js";
import {escapeHtml} from "../util.js";

export async function loadGenres(){

    const genresResponse = await apiFetch('/api/genre', {
        method: "GET"
    });

    const genresJson = await genresResponse.json();
    const genres = genresJson.genres;

    const genresContainer = document.getElementById("genres");
    genresContainer.innerHTML = genres.map((genre) => `
        <div class="genre-card">
             <a href="/genre/${genre.id}" class="genre-link">${escapeHtml(genre.name)}</a> 
        </div>
    `);
}