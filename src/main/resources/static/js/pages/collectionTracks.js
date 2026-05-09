import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {pageResponseOfSoundCollection} from "../api/collectionApi.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {renderSoundsLayout} from "../components/soundsView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initTrackCollectionPage(){
    document.title = "Коллекции треков";

    const appContainer = document.getElementById("app");

    resetPaginationState();
    paginationStateOfSounds.size = 20;

    const soundsCss = loadCss("/css/components/sounds.css");

    renderSoundsLayout(appContainer);

    const soundsHeading = appContainer.querySelector(".sounds-heading");
    const soundsContainer = appContainer.querySelector(".sounds");
    const scrollAnchor = appContainer.querySelector(".scroll-anchor");

    soundsHeading.textContent = "Моя коллекция треков";

    const likedSoundsIds = await getLikedSoundsIds();

    const pageResponse = await pageResponseOfSoundCollection();
    loadSoundsPaged(pageResponse, soundsContainer, likedSoundsIds);
    const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponse = await pageResponseOfSoundCollection();
            loadSoundsPaged(pageResponse, soundsContainer, likedSoundsIds);
        },
        hasNextFn: () => paginationStateOfSounds.hasNext,
        isLoadingFn: () => paginationStateOfSounds.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();

    return function cleanUp(){
        infiniteScroll.destroy();
        removeSoundsDelegation?.();
        unloadCss(soundsCss);
        appContainer.innerHTML = "";
    }
}