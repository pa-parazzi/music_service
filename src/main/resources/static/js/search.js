import{escapeHtml} from "./util.js";
import{loadAlbums} from "./loadAlbumsMainContent.js";
import{initAlbumContainer} from "./album/albumContainer.js";
import{playAlbums} from "./album/playAlbum.js";

const searchResults = document.querySelector(".search-results");
const notFoundResult = document.getElementById("not-found");
const foundAlbums = document.getElementById("found-albums");
const foundArtists = document.getElementById("found-artists");
const albumsContainer = document.getElementById("albums");

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

document.getElementById("search-form").addEventListener("submit", async (e) => {
    e.preventDefault(); // ⛔️ не перезагружаем страницу
    const query = document.getElementById("search-input").value.trim();

    if (!query) {
        searchResults.style.display = "none";
        albumsContainer.style.display = "flex";
        await loadAlbums(); // ⬅ если пусто → вернуть оригинальный список
        return;
    }

    const response = await fetch("/search?fragment=" + encodeURIComponent(query), {
        method: "POST"
    });

    const data = await response.json();
    albumsContainer.style.display = "none";
    searchResults.style.display = "flex";

    if (!response.ok || (data.albums.length === 0 && data.artists.length === 0)) {
        foundArtists.innerHTML = "";
        foundAlbums.innerHTML = "";
        notFoundResult.textContent = "Ничего не найдено";
        return;
    }

    foundArtists.innerHTML = ""; // очищаем прежние
    foundAlbums.innerHTML = "";
    notFoundResult.innerHTML = "";

    // Найденные исполнители
    data.artists.forEach(artist => {
        const artistCard = document.createElement("div");
        artistCard.className = "artist-card";
        artistCard.innerHTML = `<div class="artist-card">
                                    <a href="/artist/${artist.id}" class="artist-name-link">${escapeHtml(artist.name)}</a>
                                    </div>`;
        foundArtists.appendChild(artistCard);
    });

    const albums = data.albums;

    // Найденные альбомы
    await initAlbumContainer(foundAlbums, data);

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await playAlbums(albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton, currentTrackIndex, isPlaying, playAlbumButtons)
});

(async function initAlbums(){ await loadAlbums(); searchResults.style.display = "none"})();