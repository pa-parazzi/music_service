import {initSidebar} from "../module/sidebar.js";
import {initSearchForm} from "../module/search.js";
import {
    getFoundAlbumsByFragmentPaged,
    getFoundArtistsByFragmentPaged,
    getFoundSoundsByFragmentPaged
} from "../api/searchApi.js";
import {initPlayer} from "../module/player.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {getToken} from "../user/auth.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {initPlayAlbumsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";
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
import {loadArtistsPaged} from "../module/artists.js";

async function initSearchPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const searchContainer = document.getElementById("search");

    const path = window.location.pathname.split('/');
    const fragment = path[2];
    const type = path[3];

    if(!type){
        renderSearchGeneralResult(searchContainer);
        const artistsTitle = document.getElementById("artists-title");
        const albumsTitle = document.getElementById("albums-title");
        const tracksTitle = document.getElementById("tracks-title");

        const artistsContainer = document.getElementById("artists");
        const albumsContainer = document.getElementById("albums");
        const tracksContainer = document.getElementById("tracks");

        const emptyResultContainer = document.getElementById("empty-result");

        const artistsPageResponse = await getFoundArtistsByFragmentPaged(fragment);
        const artists = artistsPageResponse.content;

        const albumsPageResponse = await getFoundAlbumsByFragmentPaged(fragment);
        const albums = albumsPageResponse.content;

        const soundsPageResponse = await getFoundSoundsByFragmentPaged(fragment);
        const sounds = soundsPageResponse.content;

        if((artistsPageResponse.status === 204) &&
            (albumsPageResponse.status === 204) &&
            (soundsPageResponse.status === 204)) {
            emptyResultContainer.textContent = "По запросу " + "\"" + fragment + "\""+ " ничего не найдено";
            return;
        }

        artistsTitle.innerHTML = `<a href="/search/${fragment}/artists" class="title-link">Исполнители</a>`;
        renderArtists(artistsContainer, artists);

        albumsTitle.innerHTML = `<a href="/search/${fragment}/albums" class="title-link">Альбомы</a>`;
        renderAlbums(albumsContainer, albums);
        initPlayAlbumsDelegation(albumsContainer);

        tracksTitle.innerHTML = `<a href="/search/${fragment}/tracks" class="title-link">Треки</a>`;
        paginationStateOfSounds.sounds = sounds;
        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);
        renderSounds({container: tracksContainer, soundList: sounds, likedSoundsIds: likedSoundsIds});
        initSoundsDelegation(tracksContainer, likedSoundsIds, jwt);
        return;
    }

    if(type === 'artists'){
        resetPaginationState();
        paginationStateOfArtists.size = 10;

        renderSearchArtistsExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Исполнители по запросу " + "\"" + fragment + "\"";

        const artistsContainer = document.querySelector(".artists");

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
    } else if (type === 'albums'){
        resetPaginationState();
        paginationStateOfAlbums.size = 14;

        renderSearchAlbumsExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Альбомы по запросу " + "\"" + fragment + "\"";

        const albumsContainer = document.querySelector(".albums");

        const pageResponseOfAlbums = await getFoundAlbumsByFragmentPaged(fragment);
        loadAlbumsPaged(pageResponseOfAlbums, albumsContainer);

        initPlayAlbumsDelegation(albumsContainer);

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
    } else if (type === 'tracks'){
        resetPaginationState();
        paginationStateOfSounds.size = 20;

        renderSearchTracksExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Треки по запросу " + "\"" + fragment + "\"";

        const tracksContainer = document.querySelector(".tracks");

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        const pageResponseOfSounds = await getFoundSoundsByFragmentPaged(fragment);
        loadSoundsPaged(pageResponseOfSounds, tracksContainer, likedSoundsIds);
        initSoundsDelegation(tracksContainer, likedSoundsIds, jwt);

        const infiniteScroll = initInfiniteScroll({
            loadFn: async () => {
                const pageResponseOfSounds = await getFoundSoundsByFragmentPaged(fragment);
                loadSoundsPaged(pageResponseOfSounds, tracksContainer, likedSoundsIds);
            },
            hasNextFn: () => paginationStateOfSounds.hasNext,
            isLoadingFn: () => paginationStateOfSounds.isLoading,
            anchor: scrollAnchor
        });
        await infiniteScroll.init();
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});