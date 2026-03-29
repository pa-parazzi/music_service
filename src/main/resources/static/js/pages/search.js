import {initSidebar} from "../module/sidebar.js";
import {
    getFragmentFromUrl,
    getTypeFromUrl,
    initSearchForm,
    loadFoundAlbumsByFragment,
    loadFoundArtistsByFragment,
    loadFoundTracksByFragment
} from "../module/search.js";
import {getFoundAlbumsByFragment, getFoundArtistsByFragment, getFoundTracksByFragment} from "../api/searchApi.js";
import {initPlayer} from "../module/player.js";
import {getSoundLikes} from "../api/soundLikesApi.js";
import {getToken} from "../user/auth.js";
import {initTracksDelegation} from "../module/tracks.js";
import {initPlayAlbumsDelegation} from "../module/albums.js";
import {paginationState} from "../store/PaginationState.js";
import {renderArtists} from "../components/artistsView.js";
import {renderAlbums} from "../components/albumsView.js";
import {renderSounds} from "../components/soundsView.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {
    renderSearchAlbumsExtendedResult,
    renderSearchArtistsExtendedResult,
    renderSearchGeneralResult,
    renderSearchTracksExtendedResult
} from "../components/searchView.js";

async function initSearchPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const scrollAnchor = document.getElementById("scroll-anchor");

    const searchContainer = document.getElementById("search");

    const fragment = getFragmentFromUrl();
    const type = getTypeFromUrl();

    if(!type){
        renderSearchGeneralResult(searchContainer);
        const artistsTitle = document.getElementById("artists-title");
        const albumsTitle = document.getElementById("albums-title");
        const tracksTitle = document.getElementById("tracks-title");

        const artistsContainer = document.getElementById("artists");
        const albumsContainer = document.getElementById("albums");
        const tracksContainer = document.getElementById("tracks");

        const emptyResultContainer = document.getElementById("empty-result");

        const artistsPageResponse = await getFoundArtistsByFragment(fragment);
        const artists = artistsPageResponse.contentList;

        const albumsPageResponse = await getFoundAlbumsByFragment(fragment);
        const albums = albumsPageResponse.contentList;

        const tracksPageResponse = await getFoundTracksByFragment(fragment);
        const tracks = tracksPageResponse.contentList;

        if((artists.length === 0 && albums.length === 0 && tracks.length === 0)) {
            emptyResultContainer.textContent = "По запросу " + "\"" + fragment + "\""+ " ничего не найдено";
            return;
        }

        artistsTitle.innerHTML = `<a href="/search/${fragment}/artists" class="title-link">Исполнители</a>`;
        renderArtists(artistsContainer, artists);

        albumsTitle.innerHTML = `<a href="/search/${fragment}/albums" class="title-link">Альбомы</a>`;
        renderAlbums(albumsContainer, albums);
        initPlayAlbumsDelegation(albumsContainer);

        tracksTitle.innerHTML = `<a href="/search/${fragment}/tracks" class="title-link">Треки</a>`;
        paginationState.tracks = tracks;
        const likedSounds = await getSoundLikes(jwt);
        const likedSoundsIds = new Set(likedSounds.ids);
        renderSounds({container: tracksContainer, soundList: tracks, likedSoundsIds: likedSoundsIds});
        initTracksDelegation(tracksContainer, likedSoundsIds, jwt);
        return;
    }

    if(type === 'artists'){
        resetPaginationState();
        renderSearchArtistsExtendedResult(searchContainer);

        const title = document.getElementById("search-title");
        title.textContent = "Исполнители по запросу " + "\"" + fragment + "\"";

        const artistsInnerContainer = document.querySelector(".artists");

        await loadFoundArtistsByFragment(fragment, artistsInnerContainer);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundArtistsByFragment(fragment, artistsInnerContainer);
            },
            hasNextFn: () => paginationState.hasNext,
            isLoadingFn: () => paginationState.isLoading,
            anchor: scrollAnchor
        });
    } else if (type === 'albums'){
        resetPaginationState();
        renderSearchAlbumsExtendedResult(searchContainer);

        const title = document.getElementById("search-title");
        title.textContent = "Альбомы по запросу " + "\"" + fragment + "\"";

        const albumsInnerContainer = document.querySelector(".albums");

        await loadFoundAlbumsByFragment(fragment, albumsInnerContainer);
        initPlayAlbumsDelegation(albumsInnerContainer);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundAlbumsByFragment(fragment, albumsInnerContainer);
            },
            hasNextFn: () => paginationState.hasNext,
            isLoadingFn: () => paginationState.isLoading,
            anchor: scrollAnchor
        });
    } else if (type === 'tracks'){
        resetPaginationState();
        renderSearchTracksExtendedResult(searchContainer);

        const title = document.getElementById("search-title");
        title.textContent = "Треки по запросу " + "\"" + fragment + "\"";

        const tracksInnerContainer = document.querySelector(".tracks");

        const likedSounds = await getSoundLikes(jwt);
        const likedSoundsIds = new Set(likedSounds.ids);
        await loadFoundTracksByFragment(fragment, tracksInnerContainer, likedSoundsIds);
        initTracksDelegation(tracksInnerContainer, likedSoundsIds, jwt);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundTracksByFragment(fragment, tracksInnerContainer, likedSoundsIds);
            },
            hasNextFn: () => paginationState.hasNext,
            isLoadingFn: () => paginationState.isLoading,
            anchor: scrollAnchor
        });
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});