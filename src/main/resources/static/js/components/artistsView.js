import {escapeHtml} from "../utils/util.js";

export function renderArtists(container, artists){
    container.innerHTML = artists.map((artist) => `
       <div class="artist-card">
          <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
       </div>
    `).join('');
}

export function renderArtistsTitleLink(container){
    container.innerHTML = `<a href="/sea"></a>`
}