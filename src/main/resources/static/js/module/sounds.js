import {paginationStateOfSounds} from "../store/paginationState.js";
import {renderSounds} from "../components/soundsView.js";
import {playerState} from "../store/playerState.js";
import {setTrack, togglePlayer} from "./player.js";

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

    paginationStateOfSounds.currentPage++;
    paginationStateOfSounds.isLoading = false;
}

export function initPlaySoundButton(soundId, sound, playSoundBtn){
    if(!playSoundBtn) return;
    playSoundBtn.addEventListener("click", () => {
        if(playerState.currentSoundId !== soundId){
            playerState.currentSoundId = soundId;
            playerState.soundList = [sound];
            playerState.currentTrackIndex = 0;
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    });
}

export function initSoundsDelegation(container, likedSoundsIds = new Set(), jwt, albumId){
    container.addEventListener('click', async (e) => {
        const likeBtn = e.target.closest('.like-btn');
        if (likeBtn && container.contains(likeBtn)) {
            e.stopPropagation();

            const trackId = Number(likeBtn.dataset.trackId);
            if (likedSoundsIds.has(trackId)) {
                likeBtn.classList.add("liked");
            }
            if (likeBtn.classList.contains("liked")) {
                await fetch(`/api/sound-like/${trackId}`, {
                    method: "DELETE",
                    headers: { "Authorization": `Bearer ${jwt}` }
                });
                likedSoundsIds.delete(trackId);
                likeBtn.classList.toggle("liked", false);
            } else if (!likeBtn.classList.contains("liked")){
                await fetch(`/api/sound-like/${trackId}`, {
                    method: "POST",
                    headers: { "Authorization": `Bearer ${jwt}` }
                });
                likedSoundsIds.add(trackId);
                likeBtn.classList.toggle("liked", true);
            }
            return;
        }

        const trackEl = e.target.closest('.track-card');
        if (!trackEl) return;
        const index = Number(trackEl.dataset.index);
        if(albumId) playerState.currentAlbumId = albumId;
        playerState.soundList = paginationStateOfSounds.sounds;
        setTrack(index);
    });
    document.addEventListener('trackChanged', (e) => {
        toggleActiveTrack(container, e.detail.index);
    });
}


function toggleActiveTrack(container, index) {
    const allTracks = container.querySelectorAll('.track-card');
    allTracks.forEach(el => el.classList.remove('active'));

    const current = container.querySelector(`[data-index="${index}"]`);
    if (current) current.classList.add('active');
}