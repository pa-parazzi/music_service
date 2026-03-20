import {escapeHtml, formatTime} from "../util.js";

export function renderSounds(trackListContainer, soundList){
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
}