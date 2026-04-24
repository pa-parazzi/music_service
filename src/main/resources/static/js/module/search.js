import {renderSounds} from "../components/soundsView.js";
import {renderAlbums} from "../components/albumsView.js";
import {renderArtists} from "../components/artistsView.js";
import {getFoundAlbumsByFragment, getFoundArtistsByFragment, getFoundTracksByFragment} from "../api/searchApi.js";
import {paginationState} from "../store/PaginationState.js";
import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";

export function initSearchForm(searchForm){
    searchForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const fragment = document.getElementById("search-input").value.trim();
        if (!fragment) {
            return;
        }
        window.location.href = `/search/${encodeURIComponent(fragment)}`;
    });
}

export async function loadFoundArtistsByFragment(fragment, container){
    if(paginationStateOfArtists.isLoading || !paginationStateOfArtists.hasNext) return;
    paginationStateOfArtists.isLoading = true;

    const response = await getFoundArtistsByFragment(fragment);
    const artists = response.content;

    paginationStateOfArtists.artists.push(...artists);
    paginationStateOfArtists.hasNext = response.hasNextPage;

    renderArtists(container, artists);

    paginationStateOfArtists.currentPage++;
    paginationStateOfArtists.isLoading = false;
}

export async function loadFoundAlbumsByFragment(fragment, container){
    if(paginationStateOfAlbums.isLoading || !paginationStateOfAlbums.hasNext) return;
    paginationStateOfAlbums.isLoading = true;

    const response = await getFoundAlbumsByFragment(fragment);
    const albums = response.content;

    paginationStateOfAlbums.albums.push(...albums);
    paginationStateOfAlbums.hasNext = response.hasNextPage;

    renderAlbums(container, albums);

    paginationStateOfAlbums.currentPage++;
    paginationStateOfAlbums.isLoading = false;
}

export async function loadFoundSoundsByFragment(fragment, container, likedSoundsIds){
    if(paginationStateOfSounds.isLoading || !paginationStateOfSounds.hasNext) return;
    paginationStateOfSounds.isLoading = true;

    const response = await getFoundTracksByFragment(fragment);
    const tracks = response.content;
    const startIndex = paginationStateOfSounds.sounds.length;

    paginationStateOfSounds.sounds.push(...tracks);
    paginationStateOfSounds.hasNext = response.hasNextPage;

    renderSounds({container: container, soundList: tracks, startIndex: startIndex, likedSoundsIds: likedSoundsIds});

    paginationStateOfSounds.currentPage++;
    paginationStateOfSounds.isLoading = false;
}

export function getFragmentFromUrl(){
    const path = window.location.pathname.split('/');
    return path[2];
}

export function getTypeFromUrl(){
    const path = window.location.pathname.split('/');
    return path[3];
}