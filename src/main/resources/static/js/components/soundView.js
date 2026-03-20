import {escapeHtml} from "../utils/util.js";

export function renderSoundDetails(container, sound, trackDuration, artist, album){
    container.innerHTML = `
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
}