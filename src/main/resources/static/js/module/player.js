import {playerState} from "../store/playerState.js";
import {formatTime} from "../utils/util.js";

export function playTrack(index){
    const track = playerState.soundList[index];
    if (player.src !== track.url) {
        player.src = track.url;
    }
    player.play().catch(() => {});
    return index;
}

export function setTrack(index){
    const track = playerState.soundList[index];
    if(!track) return;
    playerState.currentTrackIndex = index;
    playerState.currentSoundId = track.id;
    playerState.currentAlbumId = track.albumId;

    playTrack(index);
    document.dispatchEvent(new CustomEvent("playerStateChanged", {
        detail: {
            soundId: playerState.currentSoundId,
            albumId: playerState.currentAlbumId
        }
    }));
}

export function togglePlayer() {
    if (player.paused) {
        player.play().catch(() => {});
    } else {
        player.pause();
    }
}

let player;
let playBtn;

export function initPlayer(audioPlayerRootContainer){
    const elements = getPlayerElements(audioPlayerRootContainer);
    const {
        nextBtn,
        prevBtn,
        progressBar,
        volumeEl,
        durationEl,
        currentTimeEl
    } = elements;

    player = elements.player;
    playBtn = elements.playBtn;

    const playerStateHandler = () => {
        if(!playBtn) return;
        playBtn.textContent = playerState.isPlaying ? "⏸" : "▶";
    }

    document.addEventListener("playerStateChanged", playerStateHandler);

    // Play / Pause
    playBtn.addEventListener("click", () => {
        if (!player.src) {
            setTrack(0);
        } else {
            togglePlayer();
        }
    });

    // Если проигрывается трек - меняем иконки
    player.addEventListener("play", () => {
        playerState.isPlaying = true;
        document.dispatchEvent(new CustomEvent("playerStateChanged"));
    });

    // Если пауза - сменили значки
    player.addEventListener("pause", () => {
        playerState.isPlaying = false;
        document.dispatchEvent(new CustomEvent("playerStateChanged"));
    });

    // По окончанию следующий трек
    player.addEventListener("ended", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            setTrack(playerState.currentTrackIndex + 1);
        }
    });

    // Следующий трек
    nextBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            setTrack(playerState.currentTrackIndex + 1);
        }
    });

    // Предыдущий трек
    prevBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex > 0) {
            setTrack(playerState.currentTrackIndex - 1);
        }
    });

    // Событие загрузки метаданных трека
    player.addEventListener("loadedmetadata", () => {
        if (!player.duration || isNaN(player.duration)) return;
        durationEl.textContent = formatTime(player.duration);
    });

    // Обновление времени и прогресс-бара
    player.addEventListener("timeupdate", () => {
        if (!player.duration || isNaN(player.duration)) return;
        progressBar.value = (player.currentTime / player.duration) * 100;
        currentTimeEl.textContent = formatTime(player.currentTime);
    });

    // Перемотка по прогресс-бару
    progressBar.addEventListener("input", () => {
        if (!player.duration) return;
        player.currentTime = (player.duration * progressBar.value) / 100;
    });

    // Громкость
    volumeEl.addEventListener("input", () => {
        player.volume = volumeEl.value;
    });
}

function getPlayerElements(audioPlayerRootContainer){
    return {
        player: audioPlayerRootContainer.querySelector(".player"),
        playBtn: audioPlayerRootContainer.querySelector(".play-btn"),
        nextBtn: audioPlayerRootContainer.querySelector(".next-btn"),
        prevBtn: audioPlayerRootContainer.querySelector(".prev-btn"),
        progressBar: audioPlayerRootContainer.querySelector(".progress-bar"),
        volumeEl: audioPlayerRootContainer.querySelector(".volume-bar"),
        durationEl: audioPlayerRootContainer.querySelector(".duration"),
        currentTimeEl: audioPlayerRootContainer.querySelector(".current-time")
    };
}