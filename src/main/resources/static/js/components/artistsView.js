import {escapeHtml} from "../utils/util.js";

export function renderArtists(container, artists){
    const html = artists.map((artist) => `
       <div class="artist-card">
          <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
       </div>
    `).join('');
    container.insertAdjacentHTML("beforeend", html);
}

export function renderArtistsLayout(container){
    container.innerHTML = `
             <h2 class="artists-heading"></h2>
             <div class="artist-rows"></div>
             <div class="scroll-anchor"></div>`;
}

export function renderArtistPage(container){
    container.innerHTML = `
    <p class="artist-name"></p>
    <div class="sounds"></div>
    <div class="scroll-anchor"></div>`;
}