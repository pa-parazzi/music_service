import {initSidebar} from "../module/sidebar.js";
import {
    renderAlbumsContainerForGenre,
    renderGeneralGenreContent,
    renderTracksContainerForGenre
} from "../components/genreView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {renderAlbums} from "../components/albumsView.js";
import {getAlbumsByGenreId, getGenreById, getTracksByGenreId} from "../api/genreApi.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {paginationState} from "../store/PaginationState.js";
import {initPlayAlbumsDelegation, loadAlbumsByGenreId} from "../module/albums.js";
import {initTracksDelegation, loadTracksByGenreId} from "../module/tracks.js";
import {renderSounds} from "../components/soundsView.js";
import {getToken} from "../user/auth.js";
import {getSoundLikes} from "../api/soundLikesApi.js";

async function initGenreDetailsPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const genrePageContainer = document.getElementById("genre-page");

    const scrollAnchor = document.getElementById("scroll-anchor");

    const path = window.location.pathname.split('/');

    const genreId = path[2];
    const type = path[3];

    const genreResponse = await getGenreById(genreId);
    const genreName = genreResponse.name;

    if(!type){
        const soundLikes = await getSoundLikes(jwt);
        const likedSoundsIds = new Set(soundLikes.ids);

        renderGeneralGenreContent(genrePageContainer, genreId);

        const tracksContainer = document.querySelector(`.tracks`);
        const albumContainer = document.querySelector(`.albums`);

        const tracksResponse = await getTracksByGenreId(genreId);
        const tracks = tracksResponse.contentList;
        paginationState.tracks = tracks;
        renderSounds({container: tracksContainer, soundList: tracks, likedSoundsIds: likedSoundsIds});
        initTracksDelegation(tracksContainer, likedSoundsIds, jwt);

        const albumsResponse = await getAlbumsByGenreId(genreId);
        const albums = albumsResponse.contentList;
        paginationState.albums = albums;
        renderAlbums(albumContainer, albums);
        initPlayAlbumsDelegation(albumContainer);

        return;
    }

    if(type==='tracks'){
        resetPaginationState();
        renderTracksContainerForGenre(genrePageContainer, genreName);
        const tracksInnerContainer = document.querySelector('.tracks');
        // первый запрос
        const soundLikes = await getSoundLikes(jwt);
        const likedSoundIds = new Set(soundLikes.ids);
        await loadTracksByGenreId(genreId, tracksInnerContainer, likedSoundIds);
        initTracksDelegation(tracksInnerContainer, likedSoundIds, jwt);
        // scroll
        initInfiniteScroll({
            loadFn: async () => {
                await loadTracksByGenreId(genreId, tracksInnerContainer, likedSoundIds);
            },
            hasNextFn: () => paginationState.hasNext,
            isLoadingFn: () => paginationState.isLoading,
            anchor: scrollAnchor
        });
    } else if(type === 'albums'){
        resetPaginationState();
        renderAlbumsContainerForGenre(genrePageContainer, genreName);
        const albumsInnerContainer = document.querySelector('.albums');
        // первый запрос
        await loadAlbumsByGenreId(genreId, albumsInnerContainer);
        initPlayAlbumsDelegation(albumsInnerContainer);
        // scroll
        initInfiniteScroll({
            loadFn: async () => {
                await loadAlbumsByGenreId(genreId, albumsInnerContainer);
            },
            hasNextFn: () => paginationState.hasNext,
            isLoadingFn: () => paginationState.isLoading,
            anchor: scrollAnchor
        });
    }
}

document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initGenreDetailsPage();
});