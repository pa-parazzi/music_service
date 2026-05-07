import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {renderAlbumCards} from "../components/albumsView.js";
import {playerState} from "../store/playerState.js";
import {getSoundsByAlbumId} from "../api/soundApi.js";
import {setTrack, togglePlayer} from "./player.js";

export function loadAlbumsPaged(pageResponse, container) {
    if (paginationStateOfAlbums.isLoading || !paginationStateOfAlbums.hasNext) return;
    paginationStateOfAlbums.isLoading = true;

    const albums = pageResponse.content;

    paginationStateOfAlbums.albums.push(...albums);
    paginationStateOfAlbums.hasNext = pageResponse.hasNextPage;

    renderAlbumCards(container, albums);

    paginationStateOfAlbums.currentPage++;
    paginationStateOfAlbums.isLoading = false;
}

export function initPlayAlbumButton(albumId, playAlbumBtn) {
    if (!playAlbumBtn) return;
    const playAlbumHandler = () => {
        if (playerState.currentAlbumId !== albumId) {
            playerState.currentAlbumId = albumId;
            playerState.soundList = paginationStateOfSounds.sounds;
            playerState.currentTrackIndex = 0;
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    }
    playAlbumBtn.addEventListener("click", playAlbumHandler);
    return function remove(){
        playAlbumBtn.removeEventListener("click", playAlbumHandler);
    }
}

export function initPlayAlbumCardsDelegation(container) {
    const albumSoundsCache = new Map();
    const playAlbumHandler = async (e) => {
        const playAlbumBtn = e.target.closest('.album-card__play-btn');
        if (!playAlbumBtn) return;
        const albumId = Number(playAlbumBtn.dataset.albumId);
        // если это новый альбом
        if (playerState.currentAlbumId !== albumId) {
            if (playerState.currentPlayAlbumButton) {
                playerState.currentPlayAlbumButton.textContent = "▶";
            }
            playerState.currentPlayAlbumButton = playAlbumBtn;
            playerState.currentAlbumId = albumId;
            playerState.currentTrackIndex = 0;
            // загружаем если ещё не загружали
            if (!albumSoundsCache.has(albumId)) {
                const soundsResponse = await getSoundsByAlbumId(albumId);
                albumSoundsCache.set(albumId, soundsResponse.sounds);
            }
            playerState.soundList = albumSoundsCache.get(albumId);
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    };
    container.addEventListener('click', playAlbumHandler);

    return function remove() {
        container.removeEventListener('click', playAlbumHandler);
    }
}