import {escapeHtml} from "./util.js"

const player = document.getElementById('player');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');

let currentTrackIndex = 0;

async function loadTrackCollection(){
    const trackCollection = document.getElementById("track-collection");

    const userId = window.currentUser.id;

    const likeResponses = await fetch('/like/get/soundLikes', {
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
          <div class="track-duration">${formatTime(Math.floor(track.duration || 0))}</div>
          <button class="like-btn" id="like-btn" data-track-id="${track.id}">&#128077;</button>
          </div>
        </div> 
      `).join('');

    const likedSounds = new Set(
        likeList.map(l => l.targetId)
    );

    document.querySelectorAll('.like-btn').forEach(btn => {
        const trackId = Number(btn.dataset.trackId);

        if (likedSounds.has(trackId)) {
            btn.classList.add("liked");
        }

        btn.addEventListener('click', async (e) => {

            e.stopPropagation();

            const likeRequest = {
                userId: userId,
                targetType: "sound",
                targetId: trackId
            };

            if(btn.classList.contains("liked")){
                const responseDeleteLike = await fetch("/like/delete", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", false);
            } else if(!btn.classList.contains("liked")){
                const responseLike = await fetch("/like", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", true);
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
    loadTrackCollection();
})();


