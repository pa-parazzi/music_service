import{escapeHtml} from "../util.js"
import{initSoundListWithLikes} from "../soundListWithLikes.js";

const player = document.getElementById('player');
const playAlbumBtn = document.getElementById('play-album');
const albumTitle = document.getElementById('album-title');
const albumArtist = document.getElementById('album-artist');
const albumImage = document.getElementById('album-image');

const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentAlbum = null;
let soundList = null;
let currentTrackIndex = 0;

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
        albumArtist.innerHTML = '';
        const link = document.createElement('a');
        link.href = `/artist/${album.artist.id}`;
        link.textContent = album.artist.name;
        link.className = 'artist-name-link';
        albumArtist.appendChild(link);

        albumImage.src = album.albumImage.url;
        albumImage.alt = escapeHtml(album.title);

        const userId = window.currentUser.id;

        const likeAlbumResponse = await fetch('/album/like/get', {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({userId})
        });

        const albumLikeBtn = document.querySelector(".album-like-btn");

        const albumId = album.albumId;

        if(likeAlbumResponse.ok){

            const likes = await likeAlbumResponse.json();

            const likedAlbums = new Set(
                likes.map(l => l.albumId)
            );

            if (likedAlbums.has(albumId)) {
                albumLikeBtn.classList.add("liked");
            }

        }

        albumLikeBtn.addEventListener('click', async (e) => {

            e.stopPropagation();

            const likeRequest = {
                userId: userId,
                targetId: albumId
            };

            if (albumLikeBtn.classList.contains("liked")) {
                const responseDeleteLike = await fetch('/album/like/delete', {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(likeRequest)
                });
                albumLikeBtn.classList.toggle("liked", false);
            } else if (!albumLikeBtn.classList.contains("liked")) {
                const responseLike = await fetch('/album/like/create', {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(likeRequest)
                });
                albumLikeBtn.classList.toggle("liked", true);
            }
        });

        soundList = await initSoundListWithLikes({
            trackList: document.getElementById("tracklist"),
            object: album
        });

        // Навешиваем обработчики клика
        document.querySelectorAll('.track').forEach(trackEl => {
            trackEl.addEventListener('click', () => {
                const index = Number(trackEl.dataset.index);
                playTrack(index);
            });
        });

        // Проигрывание альбома
        playAlbumBtn.addEventListener('click', () => {
            playTrack(0);
        });

        // ===== Следующий трек =====
        nextBtn.addEventListener("click", () => {
            if (!currentAlbum) return;
            if (currentTrackIndex < soundList.length - 1) {
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

        // Автоматически переходит к следующему треку
        player.addEventListener('ended', () => {
            if (!currentAlbum) return;
            if (currentTrackIndex < soundList.length - 1) {
                playTrack(currentTrackIndex + 1);
            }
        });

    } catch (err) {
        console.error('Ошибка загрузки альбома:', err);
    }
}

function playTrack(index) {
    if (!currentAlbum) return;
    const track = soundList[index];
    currentTrackIndex = index;
    player.src = track.url;
    player.play();

    // Обновляем визуально активный трек
    document.querySelectorAll('.track').forEach((el, i) => {
        el.classList.toggle('active', i === index);
    });

    playBtn.textContent = "⏸";
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    loadAlbum();
})();
