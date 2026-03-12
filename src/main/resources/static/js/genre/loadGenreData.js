import {apiFetch} from "../user/api.js";
import {escapeHtml} from "../util.js";

export async function loadGenreData(){

    const genreId = window.location.pathname.split("/").pop();

    const response = await fetch(`/api/genre/${genreId}`);
    if (!response.ok){
        console.log("Ошибка загрузки контента");
        return;
    }

    const genre = await response.json();

    const genreNameHeader = document.getElementById("genre-name-header");
    genreNameHeader.textContent = genre.name;

    const genreContainer = document.getElementById('genre-container');
    genreContainer.innerHTML = `
        <div class="genre-card">
             <div class="albums-${genreId}"></div>
             <div class="artists-${genreId}"></div>
        </div>`;


    const artistsResponse = await apiFetch(`/api/genre/${genreId}/artists`, {
        method: "GET"
    });
    const artistsJson = await artistsResponse.json();
    const artists = artistsJson.artists;
    renderArtists(genreId, artists);

    const albumsResponse = await apiFetch(`/api/genre/${genreId}/albums`, {
        method: "GET"
    });
    const albumsJson = await albumsResponse.json();
    const albums = albumsJson.albums;
    renderAlbums(genreId, albums);

}

function renderArtists(genreId, artists) {

    const container = document.querySelector(`.artists-${genreId}`);

    container.innerHTML = artists.map(artist => `
        <div class="artist-card">
            <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
        </div>
    `).join('');

}

function renderAlbums(genreId, albums) {

    const container = document.querySelector(`.albums-${genreId}`);

    container.innerHTML = albums.map(album => `
        <div class="album-card">
         <div class="cover-wrapper">
             <a href="/album/${album.albumId}" class="album-card-link">
             <img src="${album.albumImage.url}" alt="${escapeHtml(album.title)}" class="album-cover">
             </a>
             <button class="play-album-btn" aria-label="Play ${escapeHtml(album.title)}"></button>
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