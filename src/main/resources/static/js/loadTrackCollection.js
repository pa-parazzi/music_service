import {escapeHtml} from "./util.js"

const player = document.getElementById('player');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentTrackIndex = 0;

async function loadTrackCollection(){
    const trackCollection = document.getElementById("track-collection");

    const userId = window.currentUser.id;

    const likeResponses = await fetch('/sound/like/get', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    const likeList = await likeResponses.json();

    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likeList)
    });

    const collectionData = await trackCollectionResponse.json();

    trackCollection.innerHTML = collectionData.soundList.map((track, i) => `
        <div class="track" id="track" data-index="${i}">
          <div class="track-title">
            <span>${i + 1}</span>
            <span>${escapeHtml(track.title)}</span>
          </div>

          <div class="track-right">
          <button class="like-btn" id="like-btn" data-track-id="${track.id}">&#10133;</button>
          <div class="track-duration">${formatTime(Math.floor(track.duration || 0))}</div>
          </div>
        </div> 
      `).join('');

    const likedSounds = new Set(
        likeList.map(l => l.soundId)
    );

    document.querySelectorAll('.like-btn').forEach(btn => {
        const trackId = Number(btn.dataset.trackId);

        if (likedSounds.has(trackId)) {
            btn.classList.toggle("liked", true);
            btn.textContent = "✔";
        }

        btn.addEventListener('click', async (e) => {

            e.stopPropagation();

            const likeRequest = {
                userId: userId,
                targetId: trackId
            };

            if(btn.classList.contains("liked")){
                const responseDeleteLike = await fetch("/sound/like/delete", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", false);
                btn.textContent = "⊕";
            } else if(!btn.classList.contains("liked")){
                const responseLike = await fetch("/sound/like/create", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", true);
                btn.textContent = "✔";
            }
        });
    });

    // Навешиваем обработчики клика
    document.querySelectorAll('.track').forEach(trackEl => {
        trackEl.addEventListener('click', () => {
            const index = Number(trackEl.dataset.index);
            playTrack(index);
        });
    });

    player.addEventListener('ended', () => {
        if (currentTrackIndex < collectionData.soundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });

    function playTrack(index) {
        const track = collectionData.soundList[index];
        currentTrackIndex = index;
        player.src = track.url;
        player.play();

        document.querySelectorAll('.track').forEach((el, i) => {
            el.classList.toggle('active', i === index);
        });

        const playBtn = document.getElementById('play-btn');
        playBtn.textContent = "⏸";
    }

    // ===== Следующий трек =====
    nextBtn.addEventListener("click", () => {
        if (currentTrackIndex < collectionData.soundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });

    // ===== Предыдущий трек =====
    prevBtn.addEventListener("click", () => {
        if (currentTrackIndex > 0) {
            playTrack(currentTrackIndex - 1);
        }
    });

    // ===== Автоматический переход к следующему треку =====
    player.addEventListener("ended", () => {
        if (currentTrackIndex < collectionData.soundList.length - 1) {
            playTrack(currentTrackIndex + 1);
        }
    });
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadTrackCollection();
})();


