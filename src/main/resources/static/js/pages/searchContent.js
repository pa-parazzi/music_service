import {
    getFoundAlbumsByFragmentPaged,
    getFoundArtistsByFragmentPaged,
    getFoundSoundsByFragmentPaged
} from "../api/searchApi.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";
import {renderArtistsLayout} from "../components/artistsView.js";
import {renderAlbumsLayout} from "../components/albumsView.js";
import {renderSoundsLayout} from "../components/soundsView.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {loadArtistsPaged} from "../module/artists.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initSearchContentPage({fragment, type}) {
    document.title = "Поиск";

    resetPaginationState();

    const appContainer = document.getElementById("app");

    const handlers = {
        artists: () => initArtistsByFragmentPage(),
        albums: () => initAlbumsByFragmentPage(),
        tracks: () => initSoundsByFragmentPage()
    };

    const handler = handlers[type];
    const cleanUpFn = await handler();

    return function cleanupPage() {
        cleanUpFn?.();
    };

    async function initArtistsByFragmentPage(){
        const artistCardCss = loadCss("/css/components/artist-card.css");

        paginationStateOfArtists.size = 10;

        renderArtistsLayout(appContainer);

        const artistsHeading = appContainer.querySelector(".artists-heading");
        const artistsContainer = appContainer.querySelector(".artist-rows");
        const scrollAnchor = appContainer.querySelector(".scroll-anchor");

        artistsHeading.textContent = "Исполнители по запросу " + "\"" + fragment + "\"";

        const pageResponseOfArtists = await getFoundArtistsByFragmentPaged(fragment);
        loadArtistsPaged(pageResponseOfArtists, artistsContainer);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponseOfArtists = await getFoundArtistsByFragmentPaged(fragment);
                loadArtistsPaged(pageResponseOfArtists, artistsContainer);
            },
            hasNextFn: () => paginationStateOfArtists.hasNext,
            isLoadingFn: () => paginationStateOfArtists.isLoading,
            anchor: scrollAnchor
        });
        await infiniteScroll.init();

        return function cleanUp(){
            unloadCss(artistCardCss);
            appContainer.innerHTML = "";
        }
    }

    async function initAlbumsByFragmentPage(){
        const albumsCardCss = loadCss("/css/components/albums-card-rows.css");

        paginationStateOfAlbums.size = 14;

        renderAlbumsLayout(appContainer);

        const albumsHeading = appContainer.querySelector(".album-rows-heading");
        const albumsContainer = appContainer.querySelector(".album-rows");
        const scrollAnchor = appContainer.querySelector(".scroll-anchor");

        albumsHeading.textContent = "Альбомы по запросу " + "\"" + fragment + "\"";

        const pageResponseOfAlbums = await getFoundAlbumsByFragmentPaged(fragment);
        loadAlbumsPaged(pageResponseOfAlbums, albumsContainer);

        const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumsContainer);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponseOfAlbums = await getFoundAlbumsByFragmentPaged(fragment);
                loadAlbumsPaged(pageResponseOfAlbums, albumsContainer);
            },
            hasNextFn: () => paginationStateOfAlbums.hasNext,
            isLoadingFn: () => paginationStateOfAlbums.isLoading,
            anchor: scrollAnchor
        });
        await infiniteScroll.init();

        return function cleanUp(){
            removePlayAlbumsDelegation?.();
            unloadCss(albumsCardCss);
            appContainer.innerHTML = "";
        }
    }

    async function initSoundsByFragmentPage(){
        const soundsCss = loadCss("/css/components/sounds.css");

        paginationStateOfSounds.size = 20;

        renderSoundsLayout(appContainer);

        const soundsHeading = appContainer.querySelector(".sounds-heading");
        const soundsContainer = appContainer.querySelector(".sounds");
        const scrollAnchor = appContainer.querySelector(".scroll-anchor");

        soundsHeading.textContent = "Треки по запросу " + "\"" + fragment + "\"";

        const likedSoundsIds = await getLikedSoundsIds();

        const pageResponseOfSounds = await getFoundSoundsByFragmentPaged(fragment);
        loadSoundsPaged(pageResponseOfSounds, soundsContainer, likedSoundsIds);
        const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponseOfSounds = await getFoundSoundsByFragmentPaged(fragment);
                loadSoundsPaged(pageResponseOfSounds, soundsContainer, likedSoundsIds);
            },
            hasNextFn: () => paginationStateOfSounds.hasNext,
            isLoadingFn: () => paginationStateOfSounds.isLoading,
            anchor: scrollAnchor
        });
        await infiniteScroll.init();

        return function cleanUp(){
            removeSoundsDelegation?.();
            unloadCss(soundsCss);
            appContainer.innerHTML = "";
        }
    }
}