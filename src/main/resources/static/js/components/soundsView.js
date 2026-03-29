import {escapeHtml, formatTime} from "../utils/util.js";

export function renderSounds({container, soundList, startIndex = 0, likedSoundsIds = new Set()}){
    const html = soundList.map((track, i) => {
        const isLiked = likedSoundsIds.has(track.id) ? 'liked' : '';
        return `
        <div class="track-card" data-index="${startIndex + i}">
          <div class="track-title">
            <span>${startIndex + i + 1}</span>
              <a href="/sound/${track.id}" class="track-title-link">
                 <span>${escapeHtml(track.title)}</span>
              </a>           
          </div>
          <div class="track-right">
              <button class="like-btn ${isLiked}" data-track-id="${track.id}"></button>
              <div class="track-duration">${formatTime(Math.floor(track.duration || 0))}</div>
          </div>
        </div>
      `}).join('');
    container.insertAdjacentHTML("beforeend", html);
}