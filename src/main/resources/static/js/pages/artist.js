import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getArtistById} from "../api/artistApi.js";
import {getSoundListByArtistId} from "../api/soundApi.js";
import {getToken} from "../user/auth.js";
import {renderSounds} from "../components/soundsView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initTracksDelegation} from "../module/tracks.js";
import {paginationState} from "../store/PaginationState.js";

async function initArtistPage() {
    initPlayer();

    const jwt = getToken();
    const id = window.location.pathname.split('/').pop();
    const artist = await getArtistById(id);

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const artistName = document.getElementById('artist-name');
    artistName.textContent = artist.name;

    const tracksContainer = document.getElementById('tracklist');

    const soundList = await getSoundListByArtistId(id);
    const likedSounds = await getSoundLikes(jwt);
    const likedSoundsIds = new Set(likedSounds.ids);

    renderSounds({container: tracksContainer, soundList: soundList, likedSoundsIds: likedSoundsIds});
    paginationState.tracks = soundList;
    initTracksDelegation(tracksContainer, likedSoundsIds, jwt);
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initArtistPage();
});
