import {initSidebar} from "../module/sidebar.js";
import {initSearchForm} from "../module/search.js";
import {
    getFoundAlbumsByFragmentPaged,
    getFoundArtistsByFragmentPaged,
    getFoundSoundsByFragmentPaged
} from "../api/searchApi.js";
import {initPlayer} from "../module/player.js";
import {getLikedSoundsIds} from "../api/soundLikesApi.js";
import {getToken} from "../user/refreshAccessToken.js";
import {initSoundsDelegation, loadSoundsPaged} from "../module/sounds.js";
import {initPlayAlbumCardsDelegation, loadAlbumsPaged} from "../module/albums.js";
import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";
import {renderArtists, renderArtistsContainer} from "../components/artistsView.js";
import {renderAlbumCards, renderAlbumsContainer} from "../components/albumsView.js";
import {renderSounds, renderSoundsContainer} from "../components/soundsView.js";
import {initInfiniteScroll, resetPaginationState} from "../utils/util.js";
import {renderSearchResult} from "../components/searchView.js";
import {loadArtistsPaged} from "../module/artists.js";

async function initSearchPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const mainContainer = document.getElementById("main-container");

    const path = window.location.pathname.split('/');
    const fragment = path[2];
    const type = path[3];

    if(!type){
        renderSearchResult(mainContainer, fragment);

        const artistsContainer = mainContainer.querySelector(".artists");
        const albumsContainer = mainContainer.querySelector(".albums");
        const soundsContainer = mainContainer.querySelector(".sounds");

        const notFoundContainer = mainContainer.querySelector(".not-found");

        const artistsPageResponse = await getFoundArtistsByFragmentPaged(fragment);
        const artists = artistsPageResponse.content;

        const albumsPageResponse = await getFoundAlbumsByFragmentPaged(fragment);
        const albums = albumsPageResponse.content;

        const soundsPageResponse = await getFoundSoundsByFragmentPaged(fragment);
        const sounds = soundsPageResponse.content;

        if((artistsPageResponse.status === 204) &&
            (albumsPageResponse.status === 204) &&
            (soundsPageResponse.status === 204)) {
            notFoundContainer.textContent = "По запросу " + "\"" + fragment + "\""+ " ничего не найдено";
            return;
        }

        renderArtists(artistsContainer, artists);

        renderAlbumCards(albumsContainer, albums);
        initPlayAlbumCardsDelegation(albumsContainer);

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        renderSounds({container: soundsContainer, soundList: sounds, likedSoundsIds: likedSoundsIds});
        paginationStateOfSounds.sounds = sounds;
        initSoundsDelegation(soundsContainer, likedSoundsIds, jwt);
        return;
    }

    if(type === 'artists'){
        resetPaginationState();
        paginationStateOfArtists.size = 10;

        renderArtistsContainer(mainContainer);

        const scrollAnchor = mainContainer.querySelector(".scroll-anchor");

        const artistsHeading = mainContainer.querySelector(".artists-heading");
        artistsHeading.textContent = "Исполнители по запросу " + "\"" + fragment + "\"";

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

        renderAlbumsContainer(mainContainer);

        const scrollAnchor = mainContainer.querySelector(".scroll-anchor");

        const albumsHeading = mainContainer.querySelector(".album-rows-heading");
        albumsHeading.textContent = "Альбомы по запросу " + "\"" + fragment + "\"";

        const albumsContainer = document.querySelector(".album-rows");

        const pageResponseOfAlbums = await getFoundAlbumsByFragmentPaged(fragment);
        loadAlbumsPaged(pageResponseOfAlbums, albumsContainer);

        initPlayAlbumCardsDelegation(albumsContainer);

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

        renderSoundsContainer(mainContainer);

        const scrollAnchor = mainContainer.querySelector(".scroll-anchor");

        const soundsHeading = mainContainer.querySelector(".sounds-heading");
        soundsHeading.textContent = "Треки по запросу " + "\"" + fragment + "\"";

        const soundsContainer = document.querySelector(".sounds");

        const likedSoundsResponse = await getLikedSoundsIds(jwt);
        const likedSoundsIds = new Set(likedSoundsResponse.ids);

        const pageResponseOfSounds = await getFoundSoundsByFragmentPaged(fragment);
        loadSoundsPaged(pageResponseOfSounds, soundsContainer, likedSoundsIds);
        initSoundsDelegation(soundsContainer, likedSoundsIds, jwt);

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
    }
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initSearchPage();
});