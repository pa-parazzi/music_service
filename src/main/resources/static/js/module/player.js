import {playerState} from "../store/playerState.js";
import {formatTime} from "../utils/util.js";

export function playTracks(index){
    const track = playerState.soundList[index];
    if (!player.src || !player.src.includes(track.url)) {
        player.src = track.url;
    }
    player.play().catch(() => {});
    return index;
}

export function setTrack(index){
    playerState.currentTrackIndex = index;
    playTracks(index);
    document.dispatchEvent(new CustomEvent('trackChanged', {
        detail: {
            index
        }
    }))
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

export function initPlayer(){
    const elements = getPlayerElements();
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
        playBtn.textContent = "⏸";
        if (playerState.currentPlayAlbumButton) playerState.currentPlayAlbumButton.textContent = "⏸";
        if(playerState.currentPlaySoundButton) playerState.currentPlaySoundButton.textContent = "⏸";
    });

    // Если пауза - сменили значки
    player.addEventListener("pause", () => {
        playBtn.textContent = "▶";
        if (playerState.currentPlayAlbumButton) playerState.currentPlayAlbumButton.textContent = "▶";
        if(playerState.currentPlaySoundButton) playerState.currentPlaySoundButton.textContent = "▶";
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

function getPlayerElements(){
    return {
        player: document.getElementById('player'),
        playBtn: document.getElementById('play-btn'),
        nextBtn: document.getElementById('next-btn'),
        prevBtn: document.getElementById('prev-btn'),
        progressBar: document.getElementById('progress'),
        volumeEl: document.getElementById('volume'),
        durationEl: document.getElementById('duration'),
        currentTimeEl: document.getElementById('current-time')
    };
}