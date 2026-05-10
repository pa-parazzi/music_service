import {paginationStateOfSounds} from "../store/paginationState.js";
import {renderSounds} from "../components/soundsView.js";
import {playerState} from "../store/playerState.js";
import {setTrack, togglePlayer} from "./player.js";
import {createSoundLike, deleteSoundLike} from "../api/soundLikesApi.js";

export function loadSoundsPaged(pageResponse, container, likedSoundsIds){
    if(paginationStateOfSounds.isLoading || !paginationStateOfSounds.hasNext) return;
    paginationStateOfSounds.isLoading = true;

    const sounds = pageResponse.content;
    const startIndex = paginationStateOfSounds.sounds.length;

    paginationStateOfSounds.sounds.push(...sounds);
    paginationStateOfSounds.hasNext = pageResponse.hasNextPage;

    renderSounds({
        container: container,
        soundList: sounds,
        startIndex: startIndex,
        likedSoundsIds: likedSoundsIds
    });

    if(playerState.currentSoundId){
        toggleActiveTrack(container, playerState.currentSoundId);
    }

    paginationStateOfSounds.currentPage++;
    paginationStateOfSounds.isLoading = false;
}

export function initPlaySoundButton(soundId, sound, playSoundBtn){
    if(!playSoundBtn) return;
    const clickHandler = () => {
        if(playerState.currentSoundId !== soundId){
            playerState.currentSoundId = soundId;
            playerState.soundList = [sound];
            playerState.currentTrackIndex = 0;
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    }
    playSoundBtn.addEventListener("click", clickHandler);
    return function remove(){
        playSoundBtn.removeEventListener("click", clickHandler);
    }
}

export function initSoundsDelegation(container, likedSoundsIds = new Set(), albumId){
    const soundLikeHandler =  async (e) => {
        if(likedSoundsIds) {
            const likeBtn = e.target.closest('.like-btn');
            if (likeBtn && container.contains(likeBtn)) {
                e.stopPropagation();

                const soundId = Number(likeBtn.dataset.trackId);
                if (likeBtn.classList.contains("liked")) {
                    await deleteSoundLike(soundId);
                    likedSoundsIds.delete(soundId);
                    likeBtn.classList.toggle("liked", false);
                } else if (!likeBtn.classList.contains("liked")){
                    await createSoundLike(soundId);
                    likedSoundsIds.add(soundId);
                    likeBtn.classList.toggle("liked", true);
                }
                return;
            }
        }

        const titleLink = e.target.closest(".track-title-link");
        if(titleLink) return;

        const trackEl = e.target.closest(".track-card");
        if (!trackEl) return;

        const index = Number(trackEl.dataset.index);

        if(albumId) playerState.currentAlbumId = albumId;
        playerState.soundList = paginationStateOfSounds.sounds;
        setTrack(index);
    };

    const trackChangeHandler = (e) => {
        toggleActiveTrack(container, e.detail.soundId);
    };

    if(playerState.currentSoundId){
        toggleActiveTrack(container, playerState.currentSoundId);
    }

    container.addEventListener("click", soundLikeHandler);
    document.addEventListener("trackChanged", trackChangeHandler);

    return function remove() {
        container.removeEventListener("click", soundLikeHandler);
        document.removeEventListener("trackChanged", trackChangeHandler);
    };
}

function toggleActiveTrack(container, soundId) {
    const allTracks = container.querySelectorAll(".track-card");
    allTracks.forEach(el => el.classList.toggle("active", Number(el.dataset.trackId) === soundId));
}