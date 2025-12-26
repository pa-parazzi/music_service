import{escapeHtml} from "./util.js";

export async function initTrackList({
    trackList,
    object
})
{
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
}