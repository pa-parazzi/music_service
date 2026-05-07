export function loadAudioPlayer(container){
    container.innerHTML = `
  <div class="audio-player-root">
     <audio class="player"></audio>

     <div class="audio-player">
       <div class="player-controls">
           <button class="prev-btn">⏮</button>
           <button class="play-btn">▶</button>
           <button class="next-btn">⏭</button>
       </div>

       <div class="player-progress">
           <div class="progress-center">
               <span class="current-time">0:00</span>
               <input type="range" class="progress-bar" min="0" max="100" value="0.1">
               <span class="duration">0:00</span>
           </div>

           <div class="volume-wrapper">
               <span class="volume-icon">🔈</span>
               <input type="range" class="volume-bar" min="0" max="1" step="0.01" value="0.1">
           </div>
       </div>
     </div>
  </div>`;
    return container.querySelector(".audio-player-root");
}