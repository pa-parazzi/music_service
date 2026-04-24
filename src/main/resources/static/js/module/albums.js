import {paginationStateOfAlbums, paginationStateOfSounds} from "../store/paginationState.js";
import {renderAlbums} from "../components/albumsView.js";
import {playerState} from "../store/playerState.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";
import {setTrack, togglePlayer} from "./player.js";

export function loadAlbumsPaged(pageResponse, container){
    if(paginationStateOfAlbums.isLoading || !paginationStateOfAlbums.hasNext) return;
    paginationStateOfAlbums.isLoading = true;

    const albums = pageResponse.content;

    paginationStateOfAlbums.albums.push(...albums);
    paginationStateOfAlbums.hasNext = pageResponse.hasNextPage;

    renderAlbums(container, albums);

    paginationStateOfAlbums.currentPage++;
    paginationStateOfAlbums.isLoading = false;
}

export function initPlayAlbumButton(albumId, playAlbumBtn){
    if(!playAlbumBtn) return;
    playAlbumBtn.addEventListener("click", () => {
        if(playerState.currentAlbumId !== albumId){
            playerState.currentAlbumId = albumId;
            playerState.soundList = paginationStateOfSounds.sounds;
            playerState.currentTrackIndex = 0;
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    });
}

export function initPlayAlbumsDelegation(container){
    const albumTracksCache = new Map();
    container.addEventListener('click', async (e) => {
        const playAlbumBtn = e.target.closest('.play-album-btn');
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
            if (!albumTracksCache.has(albumId)) {
                const tracks = await getSoundListByAlbumId(albumId);
                albumTracksCache.set(albumId, tracks);
            }
            playerState.soundList = albumTracksCache.get(albumId);
            setTrack(playerState.currentTrackIndex);
            return;
        }
        togglePlayer();
    });
}