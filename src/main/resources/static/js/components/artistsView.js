import {escapeHtml} from "../utils/util.js";

export function renderArtists(container, artists){
    const html = artists.map((artist) => `
       <div class="artist-card">
          <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
       </div>
    `).join('');
    container.insertAdjacentHTML("beforeend", html);
}