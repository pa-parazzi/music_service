import{escapeHtml} from "../util.js";
import{initSoundListWithLikes} from "../soundListWithLikes.js";

const player = document.getElementById('player');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');
const artistName = document.getElementById('artist-name');
const artistTrackList = document.getElementById('tracklist');

let currentArtist = null;
let currentTrackIndex = 0;
let soundList = null;

async function loadArtist() {
    const id = window.location.pathname.split('/').pop();

    try {
        const response = await fetch(`/api/artist/${id}`);
        if (!response.ok) throw new Error("Ошибка загрузки");

        const artist = await response.json();
        currentArtist = artist;

        artistName.textContent = artist.name;
        artistName.alt = escapeHtml(artist.name);

        const soundListResponse = await fetch(`/api/sound/artist/${id}`);
        soundList = await soundListResponse.json();

        await initSoundListWithLikes({
            trackList: artistTrackList,
            soundList: soundList
        });

        // Навешиваем обработчики клика
        document.querySelectorAll('.track').forEach(trackEl => {
            trackEl.addEventListener('click', () => {
                const index = Number(trackEl.dataset.index);
                playTrack(index);
            });
        });

        player.addEventListener('ended', () => {
            if (!currentArtist) return;
            if (currentTrackIndex < soundList.length - 1) {
                playTrack(currentTrackIndex + 1);
            }
        });

        // ===== Следующий трек =====
        nextBtn.addEventListener("click", () => {
            if (!currentArtist) return;
            if (currentTrackIndex < soundList.length - 1) {
                playTrack(currentTrackIndex + 1);
            }
        });

        // ===== Предыдущий трек =====
        prevBtn.addEventListener("click", () => {
            if (!currentArtist) return;
            if (currentTrackIndex > 0) {
                playTrack(currentTrackIndex - 1);
            }
        });

        // ===== Автоматический переход к следующему треку =====
        player.addEventListener("ended", () => {
            if (!currentArtist) return;
            if (currentTrackIndex < soundList.length - 1) {
                playTrack(currentTrackIndex + 1);
            }
        });

    } catch (err) {
        console.error("Ошибка загрузки исполнителя", err);
    }
}

function playTrack(index) {
    if (!currentArtist) return;
    const track = soundList[index];
    currentTrackIndex = index;
    player.src = track.url;
    player.play();

    document.querySelectorAll('.track').forEach((el, i) => {
        el.classList.toggle('active', i === index);
    });

    const playBtn = document.getElementById('play-btn');
    playBtn.textContent = "⏸";
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadArtist();
})();
