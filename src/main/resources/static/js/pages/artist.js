import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {getArtistById} from "../api/artistApi.js";
import {getSoundsByArtistIdPaged} from "../api/soundApi.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {paginationStateOfSounds} from "../store/paginationState.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {loadCss, unloadCss} from "../core/resources.js";
import {renderArtistPage} from "../components/artistsView.js";

export async function initArtistPage({id}) {
    const soundsCss = loadCss("/css/components/sounds.css");
    const artistCss = loadCss("/css/pages/artist.css");

    resetPaginationState();
    paginationStateOfSounds.size = 10;

    const artistId = Number(id);
    const artist = await getArtistById(artistId);

    document.title = "Исполнитель: " + artist.name;

    const appContainer = document.getElementById("app");
    renderArtistPage(appContainer);

    const artistName = appContainer.querySelector(".artist-name");
    const soundsContainer = appContainer.querySelector(".sounds");
    const scrollAnchor = appContainer.querySelector(".scroll-anchor");

    artistName.textContent = artist.name;

    const likedSoundsIds = await getLikedSoundsIds();

    const pageResponseOfSounds = await getSoundsByArtistIdPaged(artistId);

    loadSoundsPaged(pageResponseOfSounds, soundsContainer, likedSoundsIds);
    const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

    const infiniteScroll = initInfiniteScroll({
        loadFn: async () => {
            const pageResponseOfSounds = await getSoundsByArtistIdPaged(artistId);
            loadSoundsPaged(pageResponseOfSounds, soundsContainer, likedSoundsIds);
        },
        hasNextFn: () => paginationStateOfSounds.hasNext,
        isLoadingFn: () => paginationStateOfSounds.isLoading,
        anchor: scrollAnchor
    });
    await infiniteScroll.init();

    return function cleanUp() {
        infiniteScroll.destroy();
        removeSoundsDelegation?.();
        unloadCss(artistCss);
        unloadCss(soundsCss);
        appContainer.innerHTML = "";
    }
}