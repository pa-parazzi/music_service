import {initSidebar} from "../module/sidebar.js";
import {
    getFragmentFromUrl,
    getTypeFromUrl,
    initSearchDetails,
    initSearchForm,
    renderSearchResult
} from "../module/search.js";
import {search} from "../api/searchApi.js";
import {initPlayer} from "../module/player.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {getToken} from "../user/auth.js";

async function initSearchPage(){
    const jwt = getToken();

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

    initSearchForm(searchForm);
    const fragment = getFragmentFromUrl();
    const type = getTypeFromUrl();
    if(!type){
        const searchData = await search(fragment);
        renderSearchResult(searchData, fragment, foundTracks, foundTracksTitle, foundAlbums,
            foundAlbumsTitle, foundArtists, foundArtistsTitle, searchDetails, notFoundResult);
        const tracks = searchData.tracks;
        const albums = searchData.albums;
        const trackCards = document.querySelectorAll('.track-card');
        const trackLikes = document.querySelectorAll('.like-btn');
        const likedSounds = await getSoundLikes(jwt);
        await initSoundLikes(likedSounds, trackLikes, jwt);
        const playAlbumButtons = document.querySelectorAll('.play-album-btn');
        await initPlayer({albums: albums, tracks: tracks, playAlbumButtons: playAlbumButtons, trackCards: trackCards});
    } else {
        await initSearchDetails(fragment, type, searchResults, searchDetails);
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});