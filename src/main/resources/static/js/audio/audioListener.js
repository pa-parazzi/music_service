import {playTracks} from "./playTracks.js";

export function audioListener(playerState, player, playBtn, nextBtn, prevBtn){

    // Навешиваем обработчики клика
    document.querySelectorAll('.track').forEach(trackEl => {
        trackEl.addEventListener('click', () => {
            const index = Number(trackEl.dataset.index);
            playerState.currentTrackIndex = playTracks(playerState.soundList, index, playBtn, player);
        });
    });

    // По окончанию следующий трек
    player.addEventListener('ended', () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            playerState.currentTrackIndex = playTracks(playerState.soundList, playerState.currentTrackIndex + 1, playBtn, player);
        }
    });

    // Следующий трек
    nextBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            playerState.currentTrackIndex = playTracks(playerState.soundList, playerState.currentTrackIndex + 1, playBtn, player);
        }
    });

    // Предыдущий трек
    prevBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex > 0) {
            playerState.currentTrackIndex = playTracks(playerState.soundList, playerState.currentTrackIndex - 1, playBtn, player);
        }
    });

}