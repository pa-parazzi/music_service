import {paginationStateOfArtists} from "../store/paginationState.js";
import {renderArtists} from "../components/artistsView.js";

export function loadArtistsPaged(pageResponse, container){
    if(paginationStateOfArtists.isLoading || !paginationStateOfArtists.hasNext) return;
    paginationStateOfArtists.isLoading = true;

    const artists = pageResponse.content;

    paginationStateOfArtists.artists.push(...artists);
    paginationStateOfArtists.hasNext = pageResponse.hasNextPage;

    renderArtists(container, artists);

    paginationStateOfArtists.currentPage++;
    paginationStateOfArtists.isLoading = false;
}