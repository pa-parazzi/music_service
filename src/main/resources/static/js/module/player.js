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

function toggleActiveTrack(index, trackCards){
    if(!trackCards || playerState.source === 'album') return;
    trackCards.forEach((trackEl, i) => {
        trackEl.classList.toggle("active", i === index);
    });
}

function setTrack(index, playBtn, player, trackCards){
    playerState.currentTrackIndex = index;
    playTracks(index, playBtn, player);
    toggleActiveTrack(index, trackCards);
}

function togglePlayer(player) {
    if (player.paused) {
        player.play().catch(() => {});
    } else {
        player.pause();
    }
}

function initPlayAlbumButton(tracks, trackCards, player, playAlbumBtn, playBtn){
    if(!playAlbumBtn) return;
    playAlbumBtn.addEventListener("click", () => {
        if (!player.src) {
            playerState.soundList = tracks;
            playerState.source = 'album';
            setTrack(0, playBtn, player, trackCards);
        } else {
            togglePlayer(player);
        }
    });
}

function initPlaySoundButton(tracks, player, playSoundBtn, playBtn){
    if(!playSoundBtn) return;
    playSoundBtn.addEventListener("click", () => {
        if (!player.src) {
            playerState.soundList = tracks;
            playerState.source = 'track';
            setTrack(0, playBtn, player, null);
        } else {
            togglePlayer(player);
        }
    });
}

export async function initPlayer(extra = {}){
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
        albums,
        tracks,
        playAlbumBtnFromSinglePage,
        playSoundBtn,
        trackCards,
        playAlbumButtons
    } = extra;

    initPlayAlbumButton(tracks, trackCards, player, playAlbumBtnFromSinglePage, playBtn);
    initPlaySoundButton(tracks, player, playSoundBtn, playBtn);
    await initPlayAlbums(albums, playAlbumButtons, playBtn, player, trackCards);

    // Play / Pause
    playBtn.addEventListener("click", () => {
        if (!player.src) {
            setTrack(0, playBtn, player, trackCards);
        } else {
            togglePlayer(player);
        }
    });

    // Если проигрывается трек - меняем иконки
    player.addEventListener("play", () => {
        playBtn.textContent = "⏸";
        if(playSoundBtn) playSoundBtn.textContent = "⏸";
        if(playAlbumBtnFromSinglePage) playAlbumBtnFromSinglePage.textContent = "⏸";
        if(playerState.currentPlayAlbumButton) playerState.currentPlayAlbumButton.textContent = "⏸";
    });

    // Если пауза - сменили значки
    player.addEventListener("pause", () => {
        playBtn.textContent = "▶";
        if(playSoundBtn) playSoundBtn.textContent = "▶";
        if(playAlbumBtnFromSinglePage) playAlbumBtnFromSinglePage.textContent = "▶";
        if(playerState.currentPlayAlbumButton) playerState.currentPlayAlbumButton.textContent = "▶";
    });

    // Клики на карточки треков
    if (trackCards) {
        trackCards.forEach((trackEl) => {
            const index = Number(trackEl.dataset.index);
            trackEl.addEventListener("click", () => {
                playerState.soundList = tracks;
                playerState.source = 'track';
                setTrack(index, playBtn, player, trackCards);
            });
        });
    }

    // По окончанию следующий трек
    player.addEventListener("ended", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            setTrack(playerState.currentTrackIndex + 1, playBtn, player, trackCards);
        }
    });

    // Следующий трек
    nextBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex < playerState.soundList.length - 1) {
            setTrack(playerState.currentTrackIndex + 1, playBtn, player, trackCards);
        }
    });

    // Предыдущий трек
    prevBtn.addEventListener("click", () => {
        if (playerState.currentTrackIndex > 0) {
            setTrack(playerState.currentTrackIndex - 1, playBtn, player, trackCards);
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

export async function initPlayAlbums(albums, playAlbumButtons, playBtn, player){
    if(!albums && !playAlbumButtons) return;
    const albumTracksCache = new Map();
    playAlbumButtons.forEach(btn => {
        btn.textContent = "▶";
        const albumId = Number(btn.dataset.albumId);
        const album = albums.find(a => a.albumId === albumId);
        btn.addEventListener('click', async () => {
            // если это новый альбом
            if (playerState.currentAlbum !== album) {
                if(playerState.currentPlayAlbumButton) playerState.currentPlayAlbumButton.textContent = "▶";
                playerState.currentAlbum = album;
                playerState.currentPlayAlbumButton = btn;
                playerState.currentTrackIndex = 0;
                // загружаем если ещё не загружали
                if (!albumTracksCache.has(albumId)) {
                    const tracks = await getSoundListByAlbumId(albumId);
                    albumTracksCache.set(albumId, tracks);
                }
                playerState.soundList = albumTracksCache.get(albumId);
                playerState.source = 'album';
                setTrack(playerState.currentTrackIndex, playBtn, player, null);
                return;
            }
            togglePlayer(player);
        });
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