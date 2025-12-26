import {escapeHtml} from "./util.js";

export async function initSoundListWithLikes({trackList, object}){

    trackList.innerHTML = object.soundList.map((track, i) => `
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

    const userId = window.currentUser.id;

    const likeResponses = await fetch('/like/get/soundLikes', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    if(likeResponses.ok){
        const likes = await likeResponses.json();

        const likedSounds = new Set(
            likes.map(l => l.targetId)
        );

        document.querySelectorAll(".like-btn").forEach(btn => {
            const trackId = Number(btn.dataset.trackId);
            if (likedSounds.has(trackId)) {
                btn.classList.add("liked");
            }
        });
    }

    document.querySelectorAll('.like-btn').forEach(btn => {

        btn.addEventListener('click', async (e) => {
            e.stopPropagation();

            const trackId = Number(btn.dataset.trackId);

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

}