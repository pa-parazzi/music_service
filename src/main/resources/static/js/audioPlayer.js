// ===== Элементы управления =====
const playBtn = document.getElementById("play-btn");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");
const progress = document.getElementById("progress");
const volume = document.getElementById("volume");
const currentTimeEl = document.getElementById("current-time");
const durationEl = document.getElementById("duration");

// Текущее состояние проигрывания
let isPlaying = false;

// ===== Функция форматирования времени (mm:ss) =====
function formatTime(seconds) {
    if (isNaN(seconds) || seconds === Infinity) return "0:00";

    seconds = Math.floor(seconds); // Убираем дроби

    const m = Math.floor(seconds / 60);
    const s = seconds % 60;

    return `${m}:${String(s).padStart(2, "0")}`;
}

// ===== Событие загрузки метаданных трека =====
player.addEventListener("loadedmetadata", () => {
    if (!player.duration || isNaN(player.duration)) return;
    durationEl.textContent = formatTime(player.duration);
});

// ===== Обновление времени и прогресс-бара =====
player.addEventListener("timeupdate", () => {
    if (!player.duration || isNaN(player.duration)) return;

    progress.value = (player.currentTime / player.duration) * 100;
    currentTimeEl.textContent = formatTime(player.currentTime);
});

// ===== Перемотка по прогресс-бару =====
progress.addEventListener("input", () => {
    if (!player.duration) return;
    player.currentTime = (player.duration * progress.value)/ 100;
});

// ===== Громкость =====
volume.addEventListener("input", () => {
    player.volume = volume.value;
});

// ===== Play / Pause =====
playBtn.addEventListener("click", () => {
    if (player.paused) {
        player.play();
        isPlaying = true;
        playBtn.textContent = "⏸";
    } else {
        player.pause();
        isPlaying = false;
        playBtn.textContent = "▶";
    }
});

// ===== Следующий трек =====
nextBtn.addEventListener("click", () => {
    if (!currentAlbum) return;
    if (currentTrackIndex < currentAlbum.soundList.length - 1) {
        playTrack(currentTrackIndex + 1);
    }
});

// ===== Предыдущий трек =====
prevBtn.addEventListener("click", () => {
    if (!currentAlbum) return;
    if (currentTrackIndex > 0) {
        playTrack(currentTrackIndex - 1);
    }
});

// ===== Автоматический переход к следующему треку =====
player.addEventListener("ended", () => {
    if (!currentAlbum) return;
    if (currentTrackIndex < currentAlbum.soundList.length - 1) {
        playTrack(currentTrackIndex + 1);
    }
});

