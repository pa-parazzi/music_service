import {escapeHtml} from "../util.js"
import {initSoundListWithLikes} from "../sound/soundListWithLikes.js";
import {playTrack} from "../audio/playTrack.js";
import {audioListener} from "../audio/audioListener.js";

const albumContent = document.getElementById('album-content')
const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

export async function loadAlbum(user) {
    const id = window.location.pathname.split('/').pop();
    const response = await fetch(`/api/album/${id}`);
    if (!response.ok) {
        console.log("Ошибка загрузки альбома");
        return;
    }
    const album = await response.json();
    const albumId = album.albumId;

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
                <button class="album-like-btn" id="album-like-btn">&#8853;</button>
            </div>
        </div>
        </div>
        <div class="tracklist" id="tracklist"></div>
    </div>`

    const soundListResponse = await fetch(`/api/sound/album/${albumId}`);
    const soundListJson = await soundListResponse.json();
    const soundList = soundListJson.soundList;

    if (!user) {
        console.log("Пользователь не авторизован");
        return;
    }
    const userId = user.id;

    await initSoundListWithLikes({
        trackListContainer: document.getElementById("tracklist"),
        soundList: soundList,
        userId: userId
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);

    const playAlbumBtn = document.querySelector(".play-album-btn");
    // Проигрывание альбома
    playAlbumBtn.addEventListener('click', () => {
        if (!player.src) {
            playerState.currentTrackIndex = playTrack(soundList, 0, playBtn, player);
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

    const likedAlbumsIdsResponse = await fetch('/api/liked-albums/get', {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({userId})
    });

    const albumLikeBtn = document.querySelector(".album-like-btn");

    if (likedAlbumsIdsResponse.ok) {
        const json = await likedAlbumsIdsResponse.json();
        const likedAlbumsIdsList = json.likedAlbumsIds;

        const likedAlbums = new Set(
            likedAlbumsIdsList.map(l => l.albumId)
        );

        if (likedAlbums.has(albumId)) {
            albumLikeBtn.classList.add("liked");
            albumLikeBtn.textContent = "✔";
        }

    }

    albumLikeBtn.addEventListener('click', async (e) => {

        e.stopPropagation();

        const likeRequest = {
            userId: userId,
            targetId: albumId
        };

        if (albumLikeBtn.classList.contains("liked")) {
            await fetch('/api/liked-albums/delete', {
                method: "DELETE",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(likeRequest)
            });
            albumLikeBtn.classList.toggle("liked", false);
            albumLikeBtn.textContent = "⊕";
        } else if (!albumLikeBtn.classList.contains("liked")) {
            await fetch('/api/liked-albums/${albumId}', {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(likeRequest)
            });
            albumLikeBtn.classList.toggle("liked", true);
            albumLikeBtn.textContent = "✔";
        }
    });
}