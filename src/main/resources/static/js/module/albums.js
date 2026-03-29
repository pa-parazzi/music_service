import {paginationState} from "../store/PaginationState.js";
import {getAlbumsByGenreId} from "../api/genreApi.js";
import {renderAlbums} from "../components/albumsView.js";
import {playerState} from "../store/playerState.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";
import {setTrack, togglePlayer} from "./player.js";

export async function loadAlbumsByGenreId(genreId, container){
    if(paginationState.isLoading || !paginationState.hasNext) return;
    paginationState.isLoading = true;

    const response = await getAlbumsByGenreId(genreId);
    const albums = response.contentList;

    paginationState.albums.push(...albums);
    paginationState.hasNext = response.hasNextPage;

    renderAlbums(container, albums);

    paginationState.currentPage++;
    paginationState.isLoading = false;
}

export function initPlayAlbumButton(albumId, playAlbumBtn){
    if(!playAlbumBtn) return;
    playAlbumBtn.addEventListener("click", () => {
        if(playerState.currentAlbumId !== albumId){
            playerState.currentAlbumId = albumId;
            playerState.soundList = paginationState.tracks;
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