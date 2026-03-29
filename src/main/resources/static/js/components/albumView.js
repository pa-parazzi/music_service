import {escapeHtml} from "../utils/util.js";

export function renderAlbum(albumContainer, album){
    albumContainer.innerHTML = `
    <div class="album-page">
        <div class="album-header">
        <div class="album-cover">
            <img id="album-image" alt="${escapeHtml(album.title)}" class="album-image" src="${album.albumImage.url}">
        </div>
        <div class="album-info">
            <div class="album-details">
                <div class="album-title">${escapeHtml(album.title)}</div>
                <a href="/artist/${album.artist.id}" class="artist-name-link">
                <div class="artist-name">${escapeHtml(album.artist.name)}</div>
                </a>
            </div>
            <div class="functionalities-of-album">
                <button class="play-album-btn" id="play-album-btn" aria-label="Play album"
                data-album-id="${album.albumId}">▶</button>
                <button class="album-like-btn" id="album-like-btn"></button>
            </div>
        </div>
        </div>
        <div class="tracklist" id="tracklist"></div>
    </div>`
}