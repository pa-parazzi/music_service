import{initAlbums} from "./albumContainer.js";
import{playAlbums} from "../audio/playAlbums.js";

const albumsContainer = document.getElementById("albums");
const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

export async function loadAlbums() {

    try {
        albumsContainer.style.display = "flex";
        const res = await fetch('/api/album');
        const data = await res.json();

        const albums = data.albums;

        console.log("Загруженные альбомы:", data);

        await initAlbums(albumsContainer, data);

        const playAlbumButtons = document.querySelectorAll('.play-album-btn');

        await playAlbums(albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton,
            currentTrackIndex, isPlaying, playAlbumButtons);

    } catch (err) {
        console.error('Ошибка загрузки альбомов:', err);
    }
}
