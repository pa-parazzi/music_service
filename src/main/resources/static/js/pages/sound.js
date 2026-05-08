import {formatTime} from "../utils/util.js";
import {getToken} from "../user/refreshAccessToken.js";
import {getSoundById} from "../api/soundApi.js";
import {getSoundLikeStatusBySoundId} from "../api/soundLikesApi.js";
import {initSoundLikeBySoundId} from "../module/soundLikes.js";
import {initPlaySoundButton} from "../module/sounds.js";
import {playerState} from "../store/playerState.js";
import {renderSoundPage} from "../components/soundsView.js";
import {loadCss, unloadCss} from "../core/resources.js";

export async function initSoundPage({id}){
    const soundPageCss = loadCss("/css/pages/sound.css");
    const albumsCardCss = loadCss("/css/components/albums-card-rows.css");

    const jwt = getToken();

    const soundId = Number(id);

    const appContainer = document.getElementById("app");

    const sound = await getSoundById(soundId);
    const artist = sound.artist;
    const album = sound.album;
    const trackDuration = formatTime(sound.duration);

    const soundPageContainer = renderSoundPage(appContainer, sound, trackDuration, artist, album);

    const playSoundBtn = soundPageContainer.querySelector(".play-sound-btn");
    const likeBtn = soundPageContainer.querySelector(".like-btn");

    playerState.currentPlaySoundButton = playSoundBtn;
    const removePlaySoundsDelegation = initPlaySoundButton(soundId, sound, playSoundBtn);

    const likeSoundStatus = await getSoundLikeStatusBySoundId(jwt, soundId);
    const removeSoundLikeDelegation = initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId);

    return function cleanUp(){
        removePlaySoundsDelegation?.();
        removeSoundLikeDelegation?.();
        unloadCss(soundPageCss);
        unloadCss(albumsCardCss);
        appContainer.innerHTML = "";
    }
}