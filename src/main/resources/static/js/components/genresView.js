import {escapeHtml} from "../utils/util.js";

export function renderGenresPage(container, genres){
    container.innerHTML = genres.map((genre) => `
        <div class="genre-card">
           <div class="genre-name">${escapeHtml(genre.name)}</div>
             <a href="/genre/${genre.id}" class="genre-link">
             <img src="/image/genre/${genre.imageName}" alt="${escapeHtml(genre.name)}" class="genre-cover">
             </a>
        </div>
    `).join('');
}

export function renderGenresWithLimit(container, genres, visibleLimit){
    const visibleGenres = genres.slice(0, visibleLimit);
    container.innerHTML = visibleGenres.map((genre) =>`
      <div class="genre-card">
          <div class="genre-cover-wrapper">
             <a href="/genre/${genre.id}" class="genre-card-link">
               <img src="/image/genre/${genre.imageName}" alt="${escapeHtml(genre.name)}" class="genre-cover">
             </a>
          </div>
          <div class="genre-meta">
             <a href="/genre/${genre.id}" class="genre-name-link">
               <div class="genre-name">${escapeHtml(genre.name)}</div>
             </a>
          </div>
      </div>`).join('');
}