import {playAlbums} from "./album/playAlbum.js";
import {initAlbumContainer} from "./album/albumContainer.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

async function loadAlbumCollection(){

    const albumCollectionContainer = document.getElementById("album-collection");

    const userId = window.currentUser.id;

    const albumLikesResponses = await fetch('/album/like/get', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    const likeList = await albumLikesResponses.json();

    const albumCollectionResponse = await fetch('/collection/albums', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likeList)
    });

    const albumData = await albumCollectionResponse.json();

    await initAlbumContainer(albumCollectionContainer, albumData);

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await playAlbums(albumData.albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton,
        currentTrackIndex, isPlaying, playAlbumButtons);

}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadAlbumCollection();
})();