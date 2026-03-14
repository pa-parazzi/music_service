import {apiFetch} from "../user/api.js";
import {escapeHtml} from "../util.js";
import {loadProfile} from "../user/loadProfile.js";
import {playAlbums} from "../audio/playAlbums.js";

async function loadGenreContent(){

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
        <div class="genre-content">
          <section class="albums-section">
            <h2 class="section-title">Альбомы</h2>
            <div class="albums-row">
                <div class="albums-${genreId}"></div>
                <a href="/genre/${genreId}/albums" class="show-all-btn">
                    Показать все
                </a>
            </div>
          </section>
          <section class="artists-section">
            <h2 class="section-title">Исполнители</h2>
            <div class="artists-container artists-${genreId}"></div>
          </section>
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
    await renderAlbums(genreId, albums);

}

function renderArtists(genreId, artists) {

    const container = document.querySelector(`.artists-${genreId}`);

    container.innerHTML = artists.map(artist => `
        <div class="artist-card">
            <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
        </div>
    `).join('');

}

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

async function renderAlbums(genreId, albums) {

    const container = document.querySelector(`.albums-${genreId}`);

    container.innerHTML = albums.slice(0, 7).map(album => `
    <div class="album-card">
         <div class="cover-wrapper">
             <a href="/album/${album.albumId}" class="album-card-link">
             <img src="${album.albumImage.url}" alt="${escapeHtml(album.title)}" class="album-cover">
             </a>
             <button class="play-album-btn" data-album-id="${album.albumId}"
              aria-label="Play ${escapeHtml(album.title)}"></button>
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

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await playAlbums(albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton,
        currentTrackIndex, isPlaying, playAlbumButtons);
}

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadGenreContent();
});