// Общий плеер на странице
const player = document.getElementById("player");
const playBtn = document.getElementById("play-btn");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");
let currentArtist = null;
let currentTrackIndex = 0;

// Используем формат из audioPlayer.js
// formatTime уже существует → повторно НЕ объявляем

async function initArtistPlayer(artist) {
    currentArtist = artist;

    document.querySelectorAll(".track").forEach(trackEl => {
        trackEl.addEventListener("click", () => {
            const index = Number(trackEl.dataset.index);
            playTrack(index);
        });
    });

    player.addEventListener("ended", () => {
        if (!currentArtist) return;
        if (currentTrackIndex < currentArtist.soundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });
}

function playTrack(index) {
    const track = currentArtist.soundList[index];
    currentTrackIndex = index;
    player.src = track.url;
    player.play();

    // подсветка
    document.querySelectorAll(".track").forEach((el, i) => {
        el.classList.toggle("active", i === index);
    });

    playBtn.textContent = "⏸"; // при старте → pause
}

// Управление кнопками
playBtn.addEventListener("click", () => {
    if (!currentArtist) return;

    if (player.paused) {
        player.play();
        playBtn.textContent = "⏸";
    } else {
        player.pause();
        playBtn.textContent = "▶";
    }
});

nextBtn.addEventListener("click", () => {
    if (!currentArtist) return;
    if (currentTrackIndex < currentArtist.soundList.length - 1) {
        playTrack(currentTrackIndex + 1);
    }
});

prevBtn.addEventListener("click", () => {
    if (!currentArtist) return;
    if (currentTrackIndex > 0) {
        playTrack(currentTrackIndex - 1);
    }
});
