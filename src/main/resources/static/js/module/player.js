import {playerState} from "../store/playerState.js";
import {formatTime} from "../utils/util.js";
import {getSoundListByAlbumId} from "../api/soundApi.js";

export function playTracks(index, playBtn, player){
    const track = playerState.soundList[index];
    if (!player.src || !player.src.includes(track.url)) {
        player.src = track.url;
    }
    player.play().catch(() => {});
    return index;
}

function togglePlayer(player) {
    if (player.paused) {
        player.play().catch(() => {});
    } else {
        player.pause();
    }
}

function initPlayAlbumButton(player, playAlbumBtn, playBtn){
    if(!playAlbumBtn) return;
    playAlbumBtn.addEventListener("click", () => {
        if (!player.src) {
            playerState.currentTrackIndex = playTracks(0, playBtn, player);
        } else {
            togglePlayer(player);
        }
    });
}

function initPlaySoundButton(player, playSoundBtn, playBtn){
    if(!playSoundBtn) return;
    playSoundBtn.addEventListener("click", () => {
        if (!player.src) {
            playerState.currentTrackIndex = playTracks(0, playBtn, player);
        } else {
            togglePlayer(player);
        }
    });
}

export function initPlayer(extra = {}){
    const elements = getPlayerElements();
    const {
        player,
        playBtn,
        nextBtn,
        prevBtn,
        progressBar,
        volumeEl,
        durationEl,
        currentTimeEl
    } = elements;
    const {
        playAlbumBtn,
        playSoundBtn,
        trackCards
    } = extra;

    initPlayAlbumButton(player, playAlbumBtn, playBtn);
    initPlaySoundButton(player, playSoundBtn, playBtn);

    // Play / Pause
    playBtn.addEventListener("click", () => {
        if (!player.src) {
            playerState.currentTrackIndex = playTracks(0, playBtn, player);
        } else {
            togglePlayer(player);
        }
    });

    // Если проигрывается трек - меняем иконки
    player.addEventListener("play", () => {
        playBtn.textContent = "⏸";
        if(playSoundBtn) playSoundBtn.textContent = "⏸";
        if(playAlbumBtn) playAlbumBtn.textContent = "⏸";
    });

    // Если пауза - сменили значки
    player.addEventListener("pause", () => {
        playBtn.textContent = "▶";
        if(playSoundBtn) playSoundBtn.textContent = "▶";
        if(playAlbumBtn) playAlbumBtn.textContent = "▶";
    });

    // Клики на карточки треков
    if(trackCards) {
        trackCards.forEach((trackEl, i) => {
            trackEl.addEventListener("click", () => {
                const index = Number(trackEl.dataset.index);
                trackEl.classList.toggle("active", i === index);
                playerState.currentTrackIndex = playTracks(index, playBtn, player);
            });
        });
    }

    // По окончанию следующий трек
    player.addEventListener("ended", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            playerState.currentTrackIndex = playTracks(playerState.currentTrackIndex + 1, playBtn, player);
        }
    });

    // Следующий трек
    nextBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            playerState.currentTrackIndex = playTracks(playerState.currentTrackIndex + 1, playBtn, player);
        }
    });

    // Предыдущий трек
    prevBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex > 0) {
            playerState.currentTrackIndex = playTracks(playerState.currentTrackIndex - 1, playBtn, player);
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
            // если это новый альбом
            if (currentAlbum !== album) {
                resetAlbumButton();
                currentAlbum = album;
                currentAlbumButton = btn;
                currentTrackIndex = 0;
                // загружаем если ещё не загружали
                if (!albumTracksCache.has(albumId)) {
                    const tracks = await getSoundListByAlbumId(albumId);
                    albumTracksCache.set(albumId, tracks);
                }
                currentSoundList = albumTracksCache.get(albumId);
                playTrack(currentTrackIndex);
                return;
            }
            togglePlayPause();
        });
    });

    playBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        togglePlayPause();
    });

    nextBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        if (currentTrackIndex < currentSoundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });

    prevBtn.addEventListener('click', () => {
        if (!currentAlbum) return;
        if (currentTrackIndex > 0) {
            playTrack(currentTrackIndex - 1);
        }
    });

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