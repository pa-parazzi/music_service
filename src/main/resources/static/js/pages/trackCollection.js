import {getSoundLikes} from "../api/soundLikesApi.js";
import {initSidebar} from "../module/sidebar.js";
import {getToken} from "../user/auth.js";
import {getSoundCollection} from "../api/soundCollectionApi.js";
import {renderSounds} from "../components/soundsView.js";
import {initSoundLikes} from "../module/soundLikes.js";
import {initSearchForm} from "../module/search.js";
import {initPlayer} from "../module/player.js";

export async function initTrackCollectionPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);

    const jwt = getToken();

    const trackCollection = document.getElementById("track-collection");

    const likedSounds = await getSoundLikes(jwt);

    const collectionData = await getSoundCollection(likedSounds);
    const soundList = collectionData.soundList;

    renderSounds(trackCollection, soundList);
    const soundLikeButtons = document.querySelectorAll('.like-btn');
    await initSoundLikes(likedSounds, soundLikeButtons, jwt);

    const trackCards = document.querySelectorAll('.track-card');
    await initPlayer({tracks: soundList, trackCards: trackCards});
}
document.addEventListener("componentsLoaded", async () => {
    initSidebar();
    await initTrackCollectionPage();
});