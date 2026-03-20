import {playTracks} from "./playTracks.js";

export function playAlbum(player, playBtn, playAlbumBtn, playerState, soundList){
    playAlbumBtn.addEventListener('click', () => {
        if (!player.src) {
            playerState.currentTrackIndex = playTracks(soundList, 0, playBtn, player);
        } else if (player.paused) {
            player.play();
        } else {
            player.pause();
        }
    });

    // Если проигрывается трек - меняем иконки на аудио-плеере и кнопке альбома
    player.addEventListener('play', () => {
        playAlbumBtn.textContent = "⏸";
        playBtn.textContent = "⏸";
    });

    // Если пауза - сменили значки
    player.addEventListener('pause', () => {
        playAlbumBtn.textContent = "▶";
        playBtn.textContent = "▶";
    });
}