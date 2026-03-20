import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getArtistById} from "../api/artistApi.js";
import {getSoundListByArtistId} from "../api/soundApi.js";
import {getToken} from "../user/auth.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {playerState} from "../store/playerState.js";

async function initArtistPage() {
    const jwt = getToken();
    const id = window.location.pathname.split('/').pop();
    const artist = await getArtistById(id);

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const artistName = document.getElementById('artist-name');
    artistName.textContent = artist.name;

    const tracksContainer = document.getElementById('tracklist');
    const soundList = await getSoundListByArtistId(id);
    renderSounds(tracksContainer, soundList);

    const likedSounds = await getSoundLikes(jwt);
    const soundLikeButtons = document.querySelectorAll('.like-btn');
    await initSoundLikes(likedSounds, soundLikeButtons, jwt);

    const trackCards = document.querySelectorAll('.track-card');
    playerState.soundList = soundList;
    initPlayer({trackCards});
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initArtistPage();
});
