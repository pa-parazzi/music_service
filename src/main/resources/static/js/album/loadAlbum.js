import {escapeHtml} from "../util.js"
import {initSounds} from "../sound/initSounds.js";
import {playTracks} from "../audio/playTracks.js";
import {audioListener} from "../audio/audioListener.js";
import {getToken} from "../user/auth.js";
import {loadSoundLikes} from "../sound/loadSoundLikes.js";
import {loadProfile} from "../user/loadProfile.js";

const albumContent = document.getElementById('album-content')
const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

export async function loadAlbum() {
    const id = window.location.pathname.split('/').pop();
    const response = await fetch(`/api/album/${id}`);
    if (!response.ok) {
        console.log("Ошибка загрузки альбома");
        return;
    }
    const album = await response.json();

    albumContent.innerHTML = `
    <div class="album-page">
        <div class="album-header">
        <div class="album-cover">
            <img id="album-image" alt="${escapeHtml(album.title)}" class="album-image" src="${album.albumImage.url}">
        </div>
        <div class="album-info">
            <div class="album-details">
                <div class="album-title">${escapeHtml(album.title)}</div>
                <a href="/artist/${album.artist.id}" class="artist-name-link">
                <div class="artist-name">${escapeHtml(album.artist.name)}</div>
                </a>
            </div>
            <div class="functionalities-of-album">
                <button class="play-album-btn" id="play-album-btn" aria-label="Play album">▶</button>
                <button class="album-like-btn" id="album-like-btn"></button>
            </div>
        </div>
        </div>
        <div class="tracklist" id="tracklist"></div>
    </div>`

    const soundListResponse = await fetch(`/api/sound/album/${id}`);
    const soundListJson = await soundListResponse.json();
    const soundList = soundListJson.soundList;

    const likedSounds = await loadSoundLikes();

    await initSounds({
        trackListContainer: document.getElementById("tracklist"),
        soundList: soundList,
        likedSounds: likedSounds
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);

    const playAlbumBtn = document.querySelector(".play-album-btn");
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

    const jwt = getToken();
    if(!jwt) return;

    const likedAlbumStatusResponse = await fetch(`/api/liked-albums/is-liked/${id}`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${jwt}`
        }
    });
    const statusLikedAlbum = await likedAlbumStatusResponse.json();

    const albumLikeBtn = document.querySelector(".album-like-btn");

    if (statusLikedAlbum.status === true) {
        albumLikeBtn.classList.add("liked");
    }

    albumLikeBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (albumLikeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-albums/${id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            albumLikeBtn.classList.toggle("liked", false);
        } else if (!albumLikeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-albums/${id}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            albumLikeBtn.classList.toggle("liked", true);
        }
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadAlbum();
});