import {renderAlbumsLayout} from "../components/albumsView.js";
import {getAlbumsByGenreIdPaged, getGenreById, getSoundsByGenreIdPaged} from "../api/genreApi.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {renderSoundsLayout} from "../components/soundsView.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {renderGenrePageContainer} from "../components/genresView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initGenreContentPage({id, type}) {

    const appContainer = document.getElementById("app");
    const genrePageContainer = renderGenrePageContainer(appContainer);

    const genreId = Number(id);

    const genreResponse = await getGenreById(genreId);
    const genreName = genreResponse.name;

    document.title = "Жанр: " + genreResponse.name;

    const handlers = {
        tracks: () => initSoundsByGenrePage(),
        albums: () => initAlbumsByGenrePage()
    };

    const handler = handlers[type];
    const cleanUpFn = await handler();

    return function cleanupPage() {
        cleanUpFn?.();
    };

    async function initAlbumsByGenrePage(){
        const albumsCardCss = loadCss("/css/components/albums-card-rows.css");

        resetPaginationState();
        paginationStateOfAlbums.size = 14;

        renderAlbumsLayout(genrePageContainer);

        const albumsHeading = genrePageContainer.querySelector(".album-rows-heading");
        const albumsContainer = genrePageContainer.querySelector(".album-rows");
        const scrollAnchor = genrePageContainer.querySelector(".scroll-anchor");

        albumsHeading.textContent = genreName;

        const pageResponse = await getAlbumsByGenreIdPaged(genreId);

        loadAlbumsPaged(pageResponse, albumsContainer);
        const removePlayAlbumsDelegation = initPlayAlbumCardsDelegation(albumsContainer);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponse = await getAlbumsByGenreIdPaged(genreId);
                loadAlbumsPaged(pageResponse, albumsContainer);
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

    async function initSoundsByGenrePage(){
        const soundsCss = loadCss("/css/components/sounds.css");

        resetPaginationState();
        paginationStateOfSounds.size = 20;

        renderSoundsLayout(genrePageContainer);

        const soundsHeading = genrePageContainer.querySelector(".sounds-heading");
        const soundsContainer = genrePageContainer.querySelector(".sounds");
        const scrollAnchor = genrePageContainer.querySelector(".scroll-anchor");

        soundsHeading.textContent = genreName;

        const likedSoundsIds = await getLikedSoundsIds();

        const pageResponse = await getSoundsByGenreIdPaged(genreId);

        loadSoundsPaged(pageResponse, soundsContainer, likedSoundsIds);
        const removeSoundsDelegation = initSoundsDelegation(soundsContainer, likedSoundsIds);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponse = await getSoundsByGenreIdPaged(genreId);
                loadSoundsPaged(pageResponse, soundsContainer, likedSoundsIds);
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