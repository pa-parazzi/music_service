import {initSidebar} from "../module/sidebar.js";
import {
    renderAlbumsContainerForGenre,
    renderGeneralGenreContent,
    renderTracksContainerForGenre
} from "../components/genreView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {renderAlbums} from "../components/albumsView.js";
import {getAlbumsByGenreIdPaged, getGenreById, getSoundsByGenreIdPaged} from "../api/genreApi.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {initPlayAlbumsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {renderSounds} from "../components/soundsView.js";
import {getToken} from "../user/auth.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";

async function initGenreDetailsPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const genrePageContainer = document.getElementById("genre-page");

    const path = window.location.pathname.split('/');

    const genreId = path[2];
    const type = path[3];

    const genreResponse = await getGenreById(genreId);
    const genreName = genreResponse.name;

    if(!type){
        resetPaginationState();

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        renderGeneralGenreContent(genrePageContainer, genreId);

        const tracksContainer = document.querySelector(`.tracks`);
        const albumContainer = document.querySelector(`.albums`);

        const pageResponse = await getSoundsByGenreIdPaged(genreId);
        const sounds = pageResponse.content;
        paginationStateOfSounds.sounds = sounds;

        renderSounds({
            container: tracksContainer,
            soundList: sounds,
            likedSoundsIds: likedSoundsIds
        });

        initSoundsDelegation(tracksContainer, likedSoundsIds, jwt);

        const albumsResponse = await getAlbumsByGenreIdPaged(genreId);
        const albums = albumsResponse.content;
        paginationStateOfAlbums.albums = albums;
        renderAlbums(albumContainer, albums);
        initPlayAlbumsDelegation(albumContainer);

        return;
    }

    if(type==='tracks'){
        resetPaginationState();
        paginationStateOfSounds.size = 10;

        renderTracksContainerForGenre(genrePageContainer, genreName);

        const scrollAnchor = document.getElementById("scroll-anchor");
        const tracksInnerContainer = document.querySelector('.tracks');

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        const pageResponse = await getSoundsByGenreIdPaged(genreId);

        loadSoundsPaged(pageResponse, tracksInnerContainer, likedSoundsIds);
        initSoundsDelegation(tracksInnerContainer, likedSoundsIds, jwt);

        initInfiniteScroll({
            loadFn: async () => {
                const pageResponse = await getSoundsByGenreIdPaged(genreId);
                loadSoundsPaged(pageResponse, tracksInnerContainer, likedSoundsIds);
            },
            hasNextFn: () => paginationStateOfSounds.hasNext,
            isLoadingFn: () => paginationStateOfSounds.isLoading,
            anchor: scrollAnchor
        });

    } else if(type === 'albums'){
        resetPaginationState();
        paginationStateOfAlbums.size = 10;

        renderAlbumsContainerForGenre(genrePageContainer, genreName);
        const scrollAnchor = document.getElementById("scroll-anchor");
        const albumsInnerContainer = document.querySelector('.albums');

        const pageResponse = await getAlbumsByGenreIdPaged(genreId);

        loadAlbumsPaged(pageResponse, albumsInnerContainer);
        initPlayAlbumsDelegation(albumsInnerContainer);

        initInfiniteScroll({
            loadFn: async () => {
                const pageResponse = await getAlbumsByGenreIdPaged(genreId);
                loadAlbumsPaged(pageResponse, albumsInnerContainer);
            },
            hasNextFn: () => paginationStateOfAlbums.hasNext,
            isLoadingFn: () => paginationStateOfAlbums.isLoading,
            anchor: scrollAnchor
        });
    }
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenreDetailsPage();
});