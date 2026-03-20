import {escapeHtml} from "../util.js";

export function renderGenres(container, genres){
    container.innerHTML = genres.map((genre) => `
        <div class="genre-card">
           <div class="genre-name">${escapeHtml(genre.name)}</div>
             <a href="/genre/${genre.id}" class="genre-link">
             <img src="/image/genre/${genre.imageName}" alt="${escapeHtml(genre.name)}" class="genre-cover">
             </a>
        </div>
    `).join('');
}