import {initPlayer} from "../module/player.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {pageResponseOfAlbumCollection} from "../api/collectionApi.js";
import {initSearchForm} from "../module/search.js";
import {initPlayAlbumsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums} from "../store/paginationState.js";

export async function initAlbumCollectionPage() {
    initPlayer();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();

    const albumCollectionContainer = document.getElementById('album-collection');
    const scrollAnchor = document.getElementById("scroll-anchor");

    resetPaginationState();
    paginationStateOfAlbums.size = 14;
    const pageResponse = await pageResponseOfAlbumCollection(jwt);
    loadAlbumsPaged(pageResponse, albumCollectionContainer);
    initPlayAlbumsDelegation(albumCollectionContainer);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await pageResponseOfAlbumCollection(jwt);
            loadAlbumsPaged(pageResponse, albumCollectionContainer);
        },
        hasNextFn: () => paginationStateOfAlbums.hasNext,
        isLoadingFn: () => paginationStateOfAlbums.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumCollectionPage();
});