import {escapeHtml} from "../utils/util.js";

export function renderAlbums(albumsContainer, albums){
    albumsContainer.innerHTML = albums.map((album) => `
     <div class="album-card">
         <div class="cover-wrapper">
             <a href="/album/${album.albumId}" class="album-card-link">
             <img src="${album.albumImage.url}" alt="${escapeHtml(album.title)}" class="album-cover">
             </a>
             <button class="play-album-btn" aria-label="Play ${escapeHtml(album.title)}" 
             data-album-id="${album.albumId}"></button>
        </div>
        <div class="album-meta">
             <a href="/album/${album.albumId}" class="album-title-link">
             <div class="album-title">${escapeHtml(album.title)}</div>
             </a>
             <a href="/artist/${album.artist.id}" class="artist-name-link">
             <div class="artist-name">${escapeHtml(album.artist.name)}</div>
             </a>
        </div>
     </div>`).join('');
}