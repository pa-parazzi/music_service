import {initSidebar} from "../module/sidebar.js";
import {
    getFragmentFromUrl,
    getTypeFromUrl,
    initSearchDetails,
    initSearchForm,
    renderSearchResult
} from "../module/search.js";
import {search} from "../api/searchApi.js";
import {initPlayer, playAlbums} from "../module/player.js";
import {playerState} from "../store/playerState.js";

let currentAlbum = null;
let currentAlbumButton = null;
let currentTrackIndex = 0;
let isPlaying = false;

async function initSearchPage(){
    const searchForm = document.getElementById("search-form");
    const searchResults = document.getElementById("search-results");
    const foundTracksTitle = document.getElementById("found-tracks-title");
    const foundTracks = document.getElementById("found-tracks");
    const foundAlbumsTitle = document.getElementById("found-albums-title");
    const foundAlbums = document.getElementById("found-albums");
    const foundArtistsTitle = document.getElementById("found-artists-title");
    const foundArtists = document.getElementById("found-artists");
    const notFoundResult = document.getElementById("not-found");
    const searchDetails = document.getElementById("search-details");

    const player = document.getElementById('player');
    const playBtn = document.getElementById('play-btn');
    const nextBtn = document.getElementById('next-btn');
    const prevBtn = document.getElementById('prev-btn');

    initSearchForm(searchForm);
    const fragment = getFragmentFromUrl();
    const type = getTypeFromUrl();
    //TODO: исправить конфликт проигрывания треков и альбомов на странице
    if(!type){
        const searchData = await search(fragment);
        renderSearchResult(searchData, fragment, foundTracks, foundTracksTitle, foundAlbums,
            foundAlbumsTitle, foundArtists, foundArtistsTitle, searchDetails, notFoundResult);
        playerState.soundList = searchData.tracks;
        const trackCards = document.querySelectorAll('.track-card');
        initPlayer({trackCards});

        const playAlbumButtons = document.querySelectorAll('.play-album-btn');
        await playAlbums(searchData.albums, player, playBtn,nextBtn, prevBtn, currentAlbum, currentAlbumButton, currentTrackIndex, isPlaying, playAlbumButtons);
    } else {
        await initSearchDetails(fragment, type, searchResults, searchDetails);
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});