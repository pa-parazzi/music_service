import {escapeHtml, formatTime} from "../utils/util.js";

export function renderSounds({container, soundList, startIndex = 0, likedSoundsIds = new Set()}){
    const html = soundList.map((track, i) => {
        let isLiked = false;
        if(likedSoundsIds) {
            isLiked = likedSoundsIds.has(track.id) ? 'liked' : '';
        }
        return `
        <div class="track-card"  data-index="${startIndex + i}" data-track-id="${track.id}" data-album-id="${track.albumId}">
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
        </div>`}).join('');
    container.insertAdjacentHTML("beforeend", html);
}

export function renderSoundsLayout(container){
    container.innerHTML = `
         <h2 class="sounds-heading"></h2>
         <div class="sounds"></div>
         <div class="scroll-anchor"></div>`;
}

export function renderSoundPage(container, sound, trackDuration, artist, album){
    container.innerHTML = `                     
            <div class="sound-page">
               <div class="sound-header">
                   <h5 class="sound-label">Трек</h5>
                   <h2 class="sound-title">${escapeHtml(sound.title)}</h2>
               </div>
               <div class="sound-body">
                  <div class="sound-metadata">
                     <div class="artist-info">
                        <h4>Исполнитель</h4>
                        <a href="/artist/${artist.id}" class="artist-name-link">
                        ${escapeHtml(artist.name)}</a>
                     </div>
                     <div class="sound-info">
                        <h4>Длительность ${trackDuration}</h4>
                        <h4>Дата релиза ${escapeHtml(sound.releaseDate)}</h4>
                     </div>
                     <div class="functionalities-of-sound">
                        <button class="play-sound-btn">▶</button>
                        <button class="like-btn"></button>
                     </div>
                  </div>
                  <div class="album-container">
                     <h3>Из альбома</h3>
                     <div class="album-card">
                        <div class="album-card-cover">
                           <a href="/album/${album.id}" class="album-card-link">
                             <img src="${album.image.url}" alt="${escapeHtml(album.title)}" class="album-card-image">
                           </a>
                        </div>
                        <div class="album-card-meta">
                           <a href="/album/${album.id}" class="album-card__title-link">
                              <div class="album-card__title">${escapeHtml(album.title)}</div>
                           </a>
                        </div>
                     </div>
                  </div>
               </div>
            </div>`;
    return container.querySelector(".sound-page");
}