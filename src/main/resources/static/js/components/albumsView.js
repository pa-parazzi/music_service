import {escapeHtml} from "../utils/util.js";

export function renderAlbumCards(container, albums){
    const html = albums.map((album) => `
     <div class="album-card">
         <div class="album-card-cover">
             <a href="/album/${album.id}" class="album-card-link">
             <img src="${album.image.url}" alt="${escapeHtml(album.title)}" class="album-card-image">
             </a>
             <button class="album-card__play-btn" aria-label="Play ${escapeHtml(album.title)}" 
             data-album-id="${album.id}">▶</button>
        </div>
        <div class="album-card-meta">
             <a href="/album/${album.id}" class="album-card__title-link">
             <div class="album-card__title">${escapeHtml(album.title)}</div>
             </a>
             <a href="/artist/${album.artist.id}" class="album-card__artist-name-link">
             <div class="album-card__artist-name">${escapeHtml(album.artist.name)}</div>
             </a>
        </div>
     </div>`).join('');
    container.insertAdjacentHTML("beforeend", html);
}

export function renderAlbumPage(container, album){
    container.innerHTML = `
    <div class="album-page">
        <div class="album-page-header">
        <div class="album-page-cover">
            <img alt="${escapeHtml(album.title)}" class="album-page-image" src="${album.image.url}">
        </div>
        <div class="album-page-info">
            <div class="album-page-details">
                <div class="album-page__title">${escapeHtml(album.title)}</div>
                <a href="/artist/${album.artist.id}" class="album-page__artist-name-link">
                <div class="album-page__artist-name">${escapeHtml(album.artist.name)}</div>
                </a>
            </div>
            <div class="functionalities-of-album">
                <button class="album-page__play-btn" id="album-page__play-btn" aria-label="Play album"
                data-album-id="${album.id}">▶</button>
                <button class="album-like-btn" id="album-like-btn"></button>
            </div>
        </div>
        </div>
        <div class="sounds" id="sounds"></div>
    </div>`;
    return container.querySelector(".album-page");
}

export function renderAlbumsLayout(container){
    container.innerHTML = `
         <h2 class="album-rows-heading"></h2>
         <div class="album-rows"></div>
         <div class="scroll-anchor"></div>`;
}
