import {initSidebar} from "../module/sidebar.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {renderAlbumCards, renderAlbumsContainer} from "../components/albumsView.js";
import {getAlbumsByGenreIdPaged, getGenreById, getSoundsByGenreIdPaged} from "../api/genreApi.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {renderSounds, renderSoundsContainer} from "../components/soundsView.js";
import {getToken} from "../user/refreshAccessToken.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {renderGenreContent} from "../components/genresView.js";

async function initGenreDetailsPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const mainContainer = document.getElementById("main-container");

    const genrePageContainer = mainContainer.querySelector(".genre-page");

    const path = window.location.pathname.split('/');

    const genreId = path[2];
    const type = path[3];

    const genreResponse = await getGenreById(genreId);
    const genreName = genreResponse.name;

    if(!type){
        resetPaginationState();

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        renderGenreContent(genrePageContainer, genreId);

        const soundsContainer = document.querySelector(`.sounds`);
        const albumContainer = document.querySelector(`.albums`);

        const pageResponse = await getSoundsByGenreIdPaged(genreId);
        const sounds = pageResponse.content;
        paginationStateOfSounds.sounds = sounds;

        renderSounds({
            container: soundsContainer,
            soundList: sounds,
            likedSoundsIds: likedSoundsIds
        });

        initSoundsDelegation(soundsContainer, likedSoundsIds, jwt);

        const albumsResponse = await getAlbumsByGenreIdPaged(genreId);
        const albums = albumsResponse.content;
        paginationStateOfAlbums.albums = albums;
        renderAlbumCards(albumContainer, albums);
        initPlayAlbumCardsDelegation(albumContainer);

        return;
    }

    if(type==='tracks'){
        resetPaginationState();
        paginationStateOfSounds.size = 20;

        renderSoundsContainer(genrePageContainer);

        const scrollAnchor = genrePageContainer.querySelector(".scroll-anchor");

        const soundsHeading = genrePageContainer.querySelector(".sounds-heading");
        soundsHeading.textContent = genreName;

        const soundsContainer = genrePageContainer.querySelector(".sounds");

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        const pageResponse = await getSoundsByGenreIdPaged(genreId);

        loadSoundsPaged(pageResponse, soundsContainer, likedSoundsIds);
        initSoundsDelegation(soundsContainer, likedSoundsIds, jwt);

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

    } else if(type === 'albums'){
        resetPaginationState();
        paginationStateOfAlbums.size = 14;

        renderAlbumsContainer(genrePageContainer);

        const scrollAnchor = genrePageContainer.querySelector(".scroll-anchor");

        const albumsHeading = genrePageContainer.querySelector(".album-rows-heading");
        albumsHeading.textContent = genreName;

        const albumsContainer = genrePageContainer.querySelector(".album-rows");

        const pageResponse = await getAlbumsByGenreIdPaged(genreId);

        loadAlbumsPaged(pageResponse, albumsContainer);
        initPlayAlbumCardsDelegation(albumsContainer);

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
    }
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenreDetailsPage();
});