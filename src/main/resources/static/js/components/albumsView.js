import {escapeHtml} from "../utils/util.js";

export function renderAlbums(container, albums){
    const html = albums.map((album) => `
     <div class="album-card">
         <div class="album-cover-wrapper">
             <a href="/album/${album.id}" class="album-card-link">
             <img src="${album.image.url}" alt="${escapeHtml(album.title)}" class="album-cover">
             </a>
             <button class="play-album-btn" aria-label="Play ${escapeHtml(album.title)}" 
             data-album-id="${album.id}">▶</button>
        </div>
        <div class="album-meta">
             <a href="/album/${album.id}" class="album-title-link">
             <div class="album-title">${escapeHtml(album.title)}</div>
             </a>
             <a href="/artist/${album.artist.id}" class="artist-name-link">
             <div class="artist-name">${escapeHtml(album.artist.name)}</div>
             </a>
        </div>
     </div>`).join('');
    container.insertAdjacentHTML("beforeend", html);
}