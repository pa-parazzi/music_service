import {initSidebar} from "../module/sidebar.js";
import {
    getFragmentFromUrl,
    getTypeFromUrl,
    initSearchForm,
    loadFoundAlbumsByFragment,
    loadFoundArtistsByFragment,
    loadFoundSoundsByFragment
} from "../module/search.js";
import {getFoundAlbumsByFragment, getFoundArtistsByFragment, getFoundSoundsByFragment} from "../api/searchApi.js";
import {initPlayer} from "../module/player.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {getToken} from "../user/auth.js";
import {initSoundsDelegation} from "../module/sounds.js";
import {initPlayAlbumsDelegation} from "../module/albums.js";
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

async function initSearchPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

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
        const artists = artistsPageResponse.content;

        const albumsPageResponse = await getFoundAlbumsByFragment(fragment);
        const albums = albumsPageResponse.content;

        const soundsPageResponse = await getFoundSoundsByFragment(fragment);
        const sounds = soundsPageResponse.content;

        if((artists.length === 0 && albums.length === 0 && sounds.length === 0)) {
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
        paginationStateOfArtists.size = 12;

        renderSearchArtistsExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Исполнители по запросу " + "\"" + fragment + "\"";

        const artistsInnerContainer = document.querySelector(".artists");

        await loadFoundArtistsByFragment(fragment, artistsInnerContainer);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundArtistsByFragment(fragment, artistsInnerContainer);
            },
            hasNextFn: () => paginationStateOfArtists.hasNext,
            isLoadingFn: () => paginationStateOfArtists.isLoading,
            anchor: scrollAnchor
        });
    } else if (type === 'albums'){
        resetPaginationState();
        paginationStateOfAlbums.size = 10;

        renderSearchAlbumsExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Альбомы по запросу " + "\"" + fragment + "\"";

        const albumsInnerContainer = document.querySelector(".albums");

        await loadFoundAlbumsByFragment(fragment, albumsInnerContainer);
        initPlayAlbumsDelegation(albumsInnerContainer);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundAlbumsByFragment(fragment, albumsInnerContainer);
            },
            hasNextFn: () => paginationStateOfAlbums.hasNext,
            isLoadingFn: () => paginationStateOfAlbums.isLoading,
            anchor: scrollAnchor
        });
    } else if (type === 'tracks'){
        resetPaginationState();
        paginationStateOfSounds.size = 10;

        renderSearchTracksExtendedResult(searchContainer);

        const scrollAnchor = document.getElementById("scroll-anchor");

        const title = document.getElementById("search-title");
        title.textContent = "Треки по запросу " + "\"" + fragment + "\"";

        const tracksInnerContainer = document.querySelector(".tracks");

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        await loadFoundSoundsByFragment(fragment, tracksInnerContainer, likedSoundsIds);
        initSoundsDelegation(tracksInnerContainer, likedSoundsIds, jwt);

        initInfiniteScroll({
            loadFn: async () => {
                await loadFoundSoundsByFragment(fragment, tracksInnerContainer, likedSoundsIds);
            },
            hasNextFn: () => paginationStateOfSounds.hasNext,
            isLoadingFn: () => paginationStateOfSounds.isLoading,
            anchor: scrollAnchor
        });
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});