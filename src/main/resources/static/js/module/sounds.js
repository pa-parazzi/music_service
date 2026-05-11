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

    syncSoundsUI(container);

    paginationStateOfSounds.currentPage++;
    paginationStateOfSounds.isLoading = false;
}

export function initPlaySoundButton(soundId, sound, playSoundBtn) {
    if (!playSoundBtn) return;
    const sync = () => {
        const isCurrent = playerState.currentSoundId === soundId;
        playSoundBtn.textContent =
            isCurrent && playerState.isPlaying ? "⏸" : "▶";
    };
    const clickHandler = () => {
        const isCurrent = playerState.currentSoundId === soundId;
        if (!isCurrent) {
            playerState.soundList = [sound];
            playerState.currentTrackIndex = 0;
            playerState.currentAlbumId = sound.album?.id ?? null;
            setTrack(0);
        } else {
            togglePlayer();
        }
    };
    playSoundBtn.addEventListener("click", clickHandler);
    document.addEventListener("playerStateChanged", sync);

    sync();

    return function remove() {
        playSoundBtn.removeEventListener("click", clickHandler);
        document.removeEventListener("playerStateChanged", sync);
    };
}

export function initSoundsDelegation(container, likedSoundsIds = new Set()){
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
        playerState.soundList = paginationStateOfSounds.sounds;
        setTrack(index);
    };

    const playerStateHandler = () => syncSoundsUI(container);

    container.addEventListener("click", soundLikeHandler);
    document.addEventListener("playerStateChanged", playerStateHandler);

    syncSoundsUI(container);

    return function remove() {
        container.removeEventListener("click", soundLikeHandler);
        document.removeEventListener("playerStateChanged", playerStateHandler);
    };
}

function syncSoundsUI(container) {
    const allTracks = container.querySelectorAll(".track-card");

    allTracks.forEach(el => {
        const id = Number(el.dataset.trackId);
        el.classList.toggle(
            "active",
            id === playerState.currentSoundId
        );
    });
}