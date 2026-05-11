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

    syncAlbumsUI(container);

    paginationStateOfAlbums.currentPage++;
    paginationStateOfAlbums.isLoading = false;
}

export function initPlayAlbumButton(albumId, playAlbumBtn) {
    if (!playAlbumBtn) return;
    const sync = () => {
        const isCurrent = playerState.currentAlbumId === albumId;
        playAlbumBtn.textContent =
            isCurrent && playerState.isPlaying ? "⏸" : "▶";
    };
    const clickHandler = () => {
        if (playerState.currentAlbumId !== albumId) {
            playerState.currentAlbumId = albumId;
            playerState.currentTrackIndex = 0;
            playerState.soundList = paginationStateOfSounds.sounds;
            setTrack(0);
        } else {
            togglePlayer();
        }
    };

    playAlbumBtn.addEventListener("click", clickHandler);
    document.addEventListener("playerStateChanged", sync);

    sync();

    return function remove(){
        playAlbumBtn.removeEventListener("click", clickHandler);
        document.removeEventListener("playerStateChanged", sync);
    };
}

export function initPlayAlbumCardsDelegation(container) {
    const albumSoundsCache = new Map();
    const playAlbumHandler = async (e) => {
        const playAlbumBtn = e.target.closest('.album-card__play-btn');
        if (!playAlbumBtn) return;
        const albumId = Number(playAlbumBtn.dataset.albumId);
        // если это новый альбом
        if (playerState.currentAlbumId !== albumId) {
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
    container.addEventListener("click", playAlbumHandler);

    const playerStateHandler = () => syncAlbumsUI(container);

    document.addEventListener("playerStateChanged", playerStateHandler);

    syncAlbumsUI(container);

    return function remove() {
        container.removeEventListener("click", playAlbumHandler);
        document.removeEventListener("playerStateChanged", playerStateHandler);
    }
}

function syncAlbumsUI(container) {
    const buttons = container.querySelectorAll(".album-card__play-btn");

    buttons.forEach(btn => {
        const id = Number(btn.dataset.albumId);
        btn.classList.toggle("active", id === playerState.currentAlbumId)
        if (id === playerState.currentAlbumId) {
            btn.textContent = playerState.isPlaying ? "⏸" : "▶";
        } else {
            btn.textContent = "▶";
        }
    });
}