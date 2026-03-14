import {escapeHtml, formatTime} from "../util.js";
import {getToken} from "../user/auth.js";

export async function initSounds({trackListContainer, soundList, likedSounds}){

    trackListContainer.innerHTML = soundList.map((track, i) => `
        <div class="track" id="track" data-index="${i}">
          <div class="track-title">
            <span>${i + 1}</span>
              <a href="/sound/${track.id}" class="track-title-link">
                 <span>${escapeHtml(track.title)}</span>
              </a>           
          </div>
          <div class="track-right">
              <button class="like-btn" data-track-id="${track.id}"></button>
              <div class="track-duration">${formatTime(Math.floor(track.duration || 0))}</div>
          </div>
        </div>
      `).join('');

    const jwt = getToken();

    const likedSoundsIds = new Set(likedSounds.ids);
    document.querySelectorAll(".like-btn").forEach(btn => {
        const trackId = Number(btn.dataset.trackId);
        if (likedSoundsIds.has(trackId)) {
            btn.classList.add("liked");
        }
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            if (btn.classList.contains("liked")) {
                await fetch(`/api/liked-sounds/${trackId}`, {
                    method: "DELETE",
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
                btn.classList.toggle("liked", false);
            } else if (!btn.classList.contains("liked")) {
                await fetch(`/api/liked-sounds/${trackId}`, {
                    method: "POST",
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
                btn.classList.toggle("liked", true);
            }
        });
    });
}