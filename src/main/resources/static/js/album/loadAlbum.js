import{escapeHtml} from "../util.js"
import{initSoundListWithLikes} from "../soundListWithLikes.js";
import{playTrack} from "../audio/playTrack.js";
import {audioListener} from "../audio/audioListener.js";

const player = document.getElementById('player');
const playAlbumBtn = document.getElementById('play-album');
const albumTitle = document.getElementById('album-title');
const artistName = document.getElementById('artist-name');
const albumImage = document.getElementById('album-image');

const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;

async function loadAlbum() {
    const id = window.location.pathname.split('/').pop();
    try {
        const response = await fetch(`/api/album/${id}`);
        if (!response.ok) throw new Error(`Ошибка загрузки: ${response.status}`);
        const album = await response.json();
        currentAlbum = album;

        // Заполняем заголовок и обложку
        albumTitle.textContent = album.title;

        // Кликабельное имя исполнителя, с переходом на страницу исполнителя
        artistName.innerHTML = '';
        const link = document.createElement('a');
        link.href = `/artist/${album.artist.id}`;
        link.textContent = album.artist.name;
        link.className = 'artist-name-link';
        artistName.appendChild(link);

        albumImage.src = album.albumImage.url;
        albumImage.alt = escapeHtml(album.title);

        const userId = window.currentUser.id;

        const likedAlbumsIdsResponse = await fetch('/api/like_album/get', {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({userId})
        });

        const albumLikeBtn = document.querySelector(".album-like-btn");

        const albumId = album.albumId;

        if(likedAlbumsIdsResponse.ok){

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
                await fetch('/api/like_album/delete', {
                    method: "DELETE",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(likeRequest)
                });
                albumLikeBtn.classList.toggle("liked", false);
                albumLikeBtn.textContent = "⊕";
            } else if (!albumLikeBtn.classList.contains("liked")) {
                await fetch('/api/like_album/create', {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(likeRequest)
                });
                albumLikeBtn.classList.toggle("liked", true);
                albumLikeBtn.textContent = "✔";
            }
        });

        const soundListResponse = await fetch(`/api/sound/album/${albumId}`);
        const soundListJson = await soundListResponse.json();
        const soundList = soundListJson.soundList;

        await initSoundListWithLikes({
            trackList: document.getElementById("tracklist"),
            soundList: soundList
        });

        const playerState = {
            currentTrackIndex: 0,
            soundList: soundList
        }

        audioListener(playerState, player, playBtn, nextBtn, prevBtn);

        // Проигрывание альбома
        playAlbumBtn.addEventListener('click', () => {
            if(!player.src){
                playerState.currentTrackIndex = playTrack(soundList, 0, playBtn, player);
            }else if(player.paused){
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

    } catch (err) {
        console.error('Ошибка загрузки альбома:', err);
    }
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadAlbum();
})();
