import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getToken} from "../user/refreshAccessToken.js";
import {pageResponseOfSoundCollection} from "../api/collectionApi.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";

export async function initTrackCollectionPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const trackCollection = document.getElementById("track-collection");
    const scrollAnchor = document.getElementById("scroll-anchor");

    resetPaginationState();
    paginationStateOfSounds.size = 20;

    const likedSoundsResponse = await getLikedSoundsIds(jwt);
    const likedSoundsIds = new Set(likedSoundsResponse.ids);

    const pageResponse = await pageResponseOfSoundCollection(jwt);

    loadSoundsPaged(pageResponse, trackCollection, likedSoundsIds);
    initSoundsDelegation(trackCollection, likedSoundsIds, jwt);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await pageResponseOfSoundCollection(jwt);
            loadSoundsPaged(pageResponse, trackCollection, likedSoundsIds);
        },
        hasNextFn: () => paginationStateOfSounds.hasNext,
        isLoadingFn: () => paginationStateOfSounds.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();
}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initTrackCollectionPage();
});