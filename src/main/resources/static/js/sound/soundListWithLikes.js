import {escapeHtml} from "../util.js";

export async function initSoundListWithLikes({trackListContainer, soundList, userId}){

    trackListContainer.innerHTML = soundList.map((track, i) => `
        <div class="track" id="track" data-index="${i}">
          <div class="track-title">
            <span>${i + 1}</span>
            <span>${escapeHtml(track.title)}</span>
          </div>

          <div class="track-right">
          <button class="like-btn" id="like-btn" data-track-id="${track.id}">&#8853;</button>
          <div class="track-duration">${formatTime(Math.floor(track.duration || 0))}</div>
          </div>
        </div>
      `).join('');

    if(userId === null){
        return;
    }

    const likedSoundsIdsResponses = await fetch('/api/liked-sounds/get', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    if(likedSoundsIdsResponses.ok){
        const json = await likedSoundsIdsResponses.json();
        const likedSoundsIdsList = json.likedSoundsIds;

        const likedSounds = new Set(
            likedSoundsIdsList.map(l => l.soundId)
        );

        document.querySelectorAll(".like-btn").forEach(btn => {
            const trackId = Number(btn.dataset.trackId);
            if (likedSounds.has(trackId)) {
                btn.classList.add("liked");
                btn.textContent = "✔";
            }
        });
    }

    document.querySelectorAll('.like-btn').forEach(btn => {

        btn.addEventListener('click', async (e) => {
            e.stopPropagation();

            const trackId = Number(btn.dataset.trackId);

            const likeRequest = {
                userId: userId,
                targetId: trackId
            };

            if(btn.classList.contains("liked")){
                await fetch("/api/liked-sounds/delete", {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", false);
                btn.textContent = "⊕";
            } else if(!btn.classList.contains("liked")){
                await fetch("/api/liked-sounds/create", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(likeRequest)
                });
                btn.classList.toggle("liked", true);
                btn.textContent = "✔";
            }
        });
    });
}