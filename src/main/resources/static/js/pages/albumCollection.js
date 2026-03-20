import {playAlbums} from "../module/player.js";
import {renderAlbums} from "../components/albumsView.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getAlbumLikes} from "../api/albumLikesApi.js";
import {getAlbumCollection} from "../api/albumCollectionApi.js";
import {initSearchForm} from "../module/search.js";

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

export async function initAlbumCollectionPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const albumCollectionContainer = document.getElementById('album-collection');
    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

    const jwt = getToken();

    const likedAlbums = await getAlbumLikes(jwt);

    const albumData = await getAlbumCollection(likedAlbums);
    const albums = albumData.albums;

    renderAlbums(albumCollectionContainer, albums);

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await playAlbums(albums, player, playBtn, nextBtn, prevBtn, currentAlbum, currentAlbumButton,
        currentTrackIndex, isPlaying, playAlbumButtons);

}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumCollectionPage();
});