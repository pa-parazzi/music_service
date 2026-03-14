import {escapeHtml, formatTime} from "../util.js";
import {loadProfile} from "../user/loadProfile.js";
import {apiFetch} from "../user/api.js";
import {getToken} from "../user/auth.js";

const player = document.getElementById("player");
const playBtn = document.getElementById("play-btn");

async function loadSound(){
    const soundId = window.location.pathname.split('/').pop();
    const soundResponse = await fetch(`/api/sound/${soundId}`);
    const sound = await soundResponse.json();
    const artist = sound.artist;
    const album = sound.album;
    const trackDuration = formatTime(sound.duration);

    const soundContainer = document.getElementById("sound");
    soundContainer.innerHTML = `
         <div class="sound-container">
              <div class="sound-meta-data">
                 <h5 class="track-title-head">Трек</h5>
                 <div class="sound-title-row">
                      <h1 class="sound-title">${escapeHtml(sound.title)}</h1>                      
                 </div>
                 <div class="sound-buttons-row">
                      <button class="play-sound-btn">▶</button>
                      <button class="like-btn"></button>
                 </div>                                 
                 <div class="track-info">
                      <h5 class="sound-duraion">Длительность ${trackDuration}</h5>
                      <h5 class="sound-release-date">Дата релиза ${escapeHtml(sound.releaseDate)}</h5>
                 </div>
              </div>
              <div class="additional-sound-info">
              <div class="artist-card">
                   <h3>Исполнитель</h3>
                   <a href="/artist/${artist.id}" class="artist-name-link">
                     ${escapeHtml(artist.name)}
                   </a>
              </div>
              <div class="album-card">
                   <h3>Из альбома</h3>
                   <div class="cover-wrapper">
                   <a href="/album/${album.id}" class="album-card-link">
                     <img src="${album.albumImageUrl}" alt="${escapeHtml(album.title)}" class="album-cover">
                   </a>
                </div>
                <div class="album-meta">
                  <a href="/album/${album.id}" class="album-title-link">
                    <div class="album-title">${escapeHtml(album.title)}</div>
                  </a>
                </div>
              </div>
              </div>
         </div>`;

    const playSoundBtn = document.querySelector('.play-sound-btn');

    playSoundBtn.addEventListener('click', () => {
        if (!player.src) {
            playTrack(sound, playBtn);
        } else if (player.paused) {
            player.play();
        } else {
            player.pause();
        }
    });

    player.addEventListener('play', () => {
        playSoundBtn.textContent = "⏸";
        playBtn.textContent = "⏸";
    });

    player.addEventListener('pause', () => {
        playSoundBtn.textContent = "▶";
        playBtn.textContent = "▶";
    });

    const likeStatusResponse = await apiFetch(`/api/liked-sounds/is-liked/${soundId}`);
    const likeSoundStatus = await likeStatusResponse.json();
    const likeBtn = document.querySelector('.like-btn');
    if (likeSoundStatus.status === true) {
        likeBtn.classList.add("liked");
    }

    const jwt = getToken();

    likeBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (likeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-sounds/${soundId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            likeBtn.classList.toggle("liked", false);
        } else if (!likeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-sounds/${soundId}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            likeBtn.classList.toggle("liked", true);
        }
    });
}

function playTrack(track, playBtn){
    player.src = track.url;
    player.play();
    playBtn.textContent = "⏸";
}

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadSound();
});