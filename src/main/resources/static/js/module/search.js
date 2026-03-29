import {renderSounds} from "../components/soundsView.js";
import {renderAlbums} from "../components/albumsView.js";
import {renderArtists} from "../components/artistsView.js";
import {getFoundAlbumsByFragment, getFoundArtistsByFragment, getFoundTracksByFragment} from "../api/searchApi.js";
import {paginationState} from "../store/PaginationState.js";

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
    if(paginationState.isLoading || !paginationState.hasNext) return;
    paginationState.isLoading = true;

    const response = await getFoundArtistsByFragment(fragment);
    const artists = response.contentList;

    paginationState.artists.push(...artists);
    paginationState.hasNext = response.hasNextPage;

    renderArtists(container, artists);

    paginationState.currentPage++;
    paginationState.isLoading = false;
}

export async function loadFoundAlbumsByFragment(fragment, container){
    if(paginationState.isLoading || !paginationState.hasNext) return;
    paginationState.isLoading = true;

    const response = await getFoundAlbumsByFragment(fragment);
    const albums = response.contentList;

    paginationState.albums.push(...albums);
    paginationState.hasNext = response.hasNextPage;

    renderAlbums(container, albums);

    paginationState.currentPage++;
    paginationState.isLoading = false;
}

export async function loadFoundTracksByFragment(fragment, container, likedSoundsIds){
    if(paginationState.isLoading || !paginationState.hasNext) return;
    paginationState.isLoading = true;

    const response = await getFoundTracksByFragment(fragment);
    const tracks = response.contentList;
    const startIndex = paginationState.tracks.length;

    paginationState.tracks.push(...tracks);
    paginationState.hasNext = response.hasNextPage;

    renderSounds({container: container, soundList: tracks, startIndex: startIndex, likedSoundsIds: likedSoundsIds});

    paginationState.currentPage++;
    paginationState.isLoading = false;
}

export function getFragmentFromUrl(){
    const path = window.location.pathname.split('/');
    return path[2];
}

export function getTypeFromUrl(){
    const path = window.location.pathname.split('/');
    return path[3];
}