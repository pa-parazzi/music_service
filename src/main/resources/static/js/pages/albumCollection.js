import {initPlayer} from "../module/player.js";
import {renderAlbums} from "../components/albumsView.js";
import {getToken} from "../user/auth.js";
import {initSidebar} from "../module/sidebar.js";
import {getAlbumLikes} from "../api/albumLikesApi.js";
import {getAlbumCollection} from "../api/albumCollectionApi.js";
import {initSearchForm} from "../module/search.js";
import {initPlayAlbumsDelegation} from "../module/albums.js";

export async function initAlbumCollectionPage(){
    initPlayer();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();

    const albumCollectionContainer = document.getElementById('album-collection');

    const likedAlbums = await getAlbumLikes(jwt);

    const albumData = await getAlbumCollection(likedAlbums);
    const albums = albumData.albums;

    renderAlbums(albumCollectionContainer, albums);
    initPlayAlbumsDelegation(albumCollectionContainer);
}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initAlbumCollectionPage();
});