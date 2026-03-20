import {renderSounds} from "../components/soundsView.js";
import {renderAlbums} from "../components/albumsView.js";
import {renderArtists} from "../components/artistsView.js";
import {getAllFoundAlbums, getAllFoundArtists, getAllFoundTracks} from "../api/searchApi.js";

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

export function renderSearchResult(searchData, fragment, foundTracks, foundTracksTitle, foundAlbums, foundAlbumsTitle,
                                         foundArtists, foundArtistsTitle, searchDetails, notFoundResult){
    const tracks = searchData.tracks;
    const albums = searchData.albums;
    const artists = searchData.artists;

    if ((tracks.length === 0 && albums.length === 0 && artists.length === 0)) {
        foundTracks.innerHTML = "";
        foundArtists.innerHTML = "";
        foundAlbums.innerHTML = "";
        notFoundResult.textContent = "Ничего не найдено";
        return;
    }

    // очищаем прежние
    foundTracks.innerHTML = "";
    foundArtists.innerHTML = "";
    foundAlbums.innerHTML = "";
    searchDetails.innerHTML = "";
    notFoundResult.innerHTML = "";

    foundTracksTitle.innerHTML = `<a href="/search/${fragment}/tracks" class="found-tracks-title-link">Треки</a>`
    // Найденные треки
    renderSounds(foundTracks, tracks);

    foundAlbumsTitle.innerHTML = `<a href="/search/${fragment}/albums" class="found-albums-title-link">Альбомы</a>`
    // Найденные альбомы
    renderAlbums(foundAlbums, albums);

    foundArtistsTitle.innerHTML = `<a href="/search/${fragment}/artists" class="found-artists-title-link">Исполнители</a>`
    // Найденные исполнители
    renderArtists(foundArtists, artists);
    return searchData;
}

export function getFragmentFromUrl(){
    const path = window.location.pathname.split('/');
    return path[2];
}

export function getTypeFromUrl(){
    const path = window.location.pathname.split('/');
    return path[3];
}

export async function initSearchDetails(fragment, type, searchMainContainer, searchDetailsContainer){
    searchMainContainer.innerHTML = "";
    searchDetailsContainer.innerHTML = "";
    if(type === "tracks"){
        const tracksResponse = await getAllFoundTracks(fragment);
        const tracks = tracksResponse.soundList;
        renderSounds(searchDetailsContainer, tracks);
    } else if(type === "albums"){
        const albumsResponse = await getAllFoundAlbums(fragment);
        const albums = albumsResponse.albums;
        renderAlbums(searchDetailsContainer, albums);
    } else if(type === "artists"){
        const artistsResponse = await getAllFoundArtists(fragment);
        const artists = artistsResponse.artists;
        renderArtists(searchDetailsContainer, artists);
    }
}