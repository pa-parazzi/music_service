
export async function playAlbums(
    albums,
    player,
    playBtn,
    nextBtn,
    prevBtn,
    currentAlbum,
    currentAlbumButton,
    currentTrackIndex,
    isPlaying,
    playAlbumButtons
){

    let currentSoundList = null;

    playAlbumButtons.forEach(btn => {

        btn.textContent = "▶";
        const albumId = Number(btn.dataset.albumId);
        const album = albums.find(a => a.albumId === albumId);

        const albumTracksCache = new Map();

        btn.addEventListener('click', async () => {
            // 👉 если это НОВЫЙ альбом
            if (currentAlbum !== album) {

                resetAlbumButton();

                currentAlbum = album;
                currentAlbumButton = btn;
                currentTrackIndex = 0;

                // 🔹 загружаем ТОЛЬКО если ещё не загружали
                if (!albumTracksCache.has(albumId)) {
                    const response = await fetch(`/api/sound/album/${albumId}`);
                    const tracks = await response.json();
                    albumTracksCache.set(albumId, tracks);
                }

                currentSoundList = albumTracksCache.get(albumId);

                playTrack(currentTrackIndex);
                return;
            }
            // 👉 Тот же альбом → toggle play / pause
            togglePlayPause();
        });
    });

// ====== КНОПКА PLAY / PAUSE ПЛЕЕРА ======
    playBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        togglePlayPause();
    });


// ====== NEXT ======
    nextBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        if (currentTrackIndex < currentSoundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });

// ====== PREV ======
    prevBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        if (currentTrackIndex > 0) {
            playTrack(currentTrackIndex - 1);
        }
    });

// ====== АВТОПЕРЕКЛЮЧЕНИЕ ПО ОКОНЧАНИЮ ======
    player.addEventListener('ended', () => {
        if (!currentAlbum) return;
        if (currentTrackIndex < currentSoundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        } else {
            setPlayingState(false);
        }
    });

    function playTrack(index) {
        const track = currentSoundList[index];
        currentTrackIndex = index;

        player.src = track.url;
        player.play();

        setPlayingState(true);
    }

    function togglePlayPause() {
        if (isPlaying) {
            player.pause();
            setPlayingState(false);
        } else {
            player.play();
            setPlayingState(true);
        }
    }

    function setPlayingState(playing) {
        isPlaying = playing;

        // кнопка плеера
        playBtn.textContent = playing ? "⏸" : "▶";

        // кнопка альбома
        if (currentAlbumButton) {
            currentAlbumButton.textContent = playing ? "⏸" : "▶";
            currentAlbumButton.classList.toggle("playing", playing);
        }
    }

    function resetAlbumButton() {
        if (!currentAlbumButton) return;

        currentAlbumButton.textContent = "▶";
        currentAlbumButton.classList.remove("playing");
    }
}
