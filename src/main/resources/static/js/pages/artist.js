import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getArtistById} from "../api/artistApi.js";
import {getSoundsByArtistIdPaged} from "../api/soundApi.js";
import {getToken} from "../user/auth.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";

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
    const scrollAnchor = document.getElementById("scroll-anchor");

    resetPaginationState();
    paginationStateOfSounds.size = 10;

    const likedSoundsResponse = await getLikedSoundsIds(jwt);
    const likedSoundsIds = new Set(likedSoundsResponse.ids);

    const pageResponse = await getSoundsByArtistIdPaged(id);

    loadSoundsPaged(pageResponse, tracksContainer, likedSoundsIds);
    initSoundsDelegation(tracksContainer, likedSoundsIds, jwt);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await getSoundsByArtistIdPaged(id);
            loadSoundsPaged(pageResponse, tracksContainer, likedSoundsIds);
        },
        hasNextFn: () => paginationStateOfSounds.hasNext,
        isLoadingFn: () => paginationStateOfSounds.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initArtistPage();
});
