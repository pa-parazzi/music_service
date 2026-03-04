import {playAlbums} from "../audio/playAlbums.js";
import {initAlbums} from "../album/initAlbums.js";
import {getToken} from "../user/auth.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

export async function loadAlbumCollection(){

    const jwt = getToken();

    const albumCollectionContainer = document.getElementById("album-collection");

    const likedAlbumsIdsResponse = await fetch('/api/liked-albums', {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });

    const likedAlbums = await likedAlbumsIdsResponse.json();

    const albumCollectionResponse = await fetch('/collection/albums', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likedAlbums)
    });

    const albumData = await albumCollectionResponse.json();

    await initAlbums(albumCollectionContainer, albumData);

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await playAlbums(albumData.albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton,
        currentTrackIndex, isPlaying, playAlbumButtons);

}