import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getToken} from "../user/auth.js";
import {getSoundCollection} from "../api/soundCollectionApi.js";
import {renderSounds} from "../components/soundsView.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";
import {initTracksDelegation} from "../module/tracks.js";
import {paginationState} from "../store/PaginationState.js";

export async function initTrackCollectionPage(){
    initPlayer();
    const jwt = getToken();

    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const trackCollection = document.getElementById("track-collection");

    const likedSounds = await getSoundLikes(jwt);
    const likedSoundIds = new Set(likedSounds.ids);

    const collectionData = await getSoundCollection(likedSounds);
    const soundList = collectionData.soundList;

    renderSounds({container: trackCollection, soundList: soundList, likedSoundsIds: likedSoundIds});
    paginationState.tracks = soundList;
    initTracksDelegation(trackCollection, likedSoundIds, jwt);
}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initTrackCollectionPage();
});