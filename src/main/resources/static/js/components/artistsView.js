import {escapeHtml} from "../utils/util.js";

export function renderArtists(container, artists){
    const html = artists.map((artist) => `
       <div class="artist-card">
          <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
       </div>
    `).join('');
    container.insertAdjacentHTML("beforeend", html);
}

export function renderArtistsContainer(container){
    container.innerHTML = `
             <h2 class="artists-heading"></h2>
             <div class="artists"></div>
             <div class="scroll-anchor"></div>`;
}