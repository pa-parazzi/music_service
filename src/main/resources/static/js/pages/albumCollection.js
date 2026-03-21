import {initPlayer} from "../module/player.js";
import {renderAlbums} from "../components/albumsView.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getAlbumLikes} from "../api/albumLikesApi.js";
import {getAlbumCollection} from "../api/albumCollectionApi.js";
import {initSearchForm} from "../module/search.js";

export async function initAlbumCollectionPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const albumCollectionContainer = document.getElementById('album-collection');

    const jwt = getToken();

    const likedAlbums = await getAlbumLikes(jwt);

    const albumData = await getAlbumCollection(likedAlbums);
    const albums = albumData.albums;

    renderAlbums(albumCollectionContainer, albums);

    const playAlbumButtons = document.querySelectorAll('.play-album-btn');

    await initPlayer({albums: albums, playAlbumButtons: playAlbumButtons});

}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumCollectionPage();
});