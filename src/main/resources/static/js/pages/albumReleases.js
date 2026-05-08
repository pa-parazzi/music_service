import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums} from "../store/paginationState.js";
import {loadCss, unloadCss} from "../core/resources.js";
import {renderAlbumsLayout} from "../components/albumsView.js";
import {getNewAlbumReleasesPaged} from "../api/albumApi.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";

export async function initAlbumReleasesPage(){
    resetPaginationState();
    paginationStateOfAlbums.size = 14;

    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");

    document.title = "Новинки";

    const appContainer = document.getElementById("app");

    renderAlbumsLayout(appContainer);

    const scrollAnchor = appContainer.querySelector(".scroll-anchor");

    const albumsHeading = appContainer.querySelector(".album-rows-heading");
    albumsHeading.textContent = "Новинки этого года";

    const albumsEl = appContainer.querySelector(".album-rows");

    const pageResponse = await getNewAlbumReleasesPaged();
    loadAlbumsPaged(pageResponse, albumsEl);

    initPlayAlbumCardsDelegation(albumsEl);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await getNewAlbumReleasesPaged();
            loadAlbumsPaged(pageResponse, albumsEl);
        },
        hasNextFn: () => paginationStateOfAlbums.hasNext,
        isLoadingFn: () => paginationStateOfAlbums.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();

    return function cleanup() {
        infiniteScroll.destroy();
        unloadCss(albumsCardCss);
        appContainer.innerHTML = "";
    };
}