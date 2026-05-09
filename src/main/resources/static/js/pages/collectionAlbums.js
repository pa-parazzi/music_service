import {pageResponseOfAlbumCollection} from "../api/collectionApi.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums} from "../store/paginationState.js";
import {renderAlbumsLayout} from "../components/albumsView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initAlbumCollectionPage() {

    document.title = "Коллекции альбомов";

    const appContainer = document.getElementById("app");

    resetPaginationState();
    paginationStateOfAlbums.size = 14;

    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");

    renderAlbumsLayout(appContainer);

    const albumRowsHeading = appContainer.querySelector(".album-rows-heading");
    const albumRowsContainer = appContainer.querySelector(".album-rows");
    const scrollAnchor = appContainer.querySelector(".scroll-anchor");

    albumRowsHeading.textContent = "Моя коллекция альбомов";

    const pageResponse = await pageResponseOfAlbumCollection();
    loadAlbumsPaged(pageResponse, albumRowsContainer);

    const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumRowsContainer);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await pageResponseOfAlbumCollection();
            loadAlbumsPaged(pageResponse, albumRowsContainer);
        },
        hasNextFn: () => paginationStateOfAlbums.hasNext,
        isLoadingFn: () => paginationStateOfAlbums.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();

    return function cleanUp(){
        infiniteScroll.destroy();
        removePlayAlbumsDelegation?.();
        unloadCss(albumsCardCss);
        appContainer.innerHTML = "";
    }
}