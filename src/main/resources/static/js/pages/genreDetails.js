import {apiFetch} from "../user/api.js";
import {escapeHtml} from "../util.js";
import {playAlbums} from "../module/playAlbums.js";
import {initSidebar} from "../module/sidebar.js";
import {renderGenreDetails} from "../components/genreDetailsView";

//TODO: редактировать содержимое страницы
async function genreDetails(){
    const genreId = window.location.pathname.split("/").pop();

    const genreContainer = document.getElementById('genre-container');
    renderGenreDetails(genreContainer, genreId);

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

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

async function renderAlbums(genreId, albums) {

    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

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

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await genreDetails();
});