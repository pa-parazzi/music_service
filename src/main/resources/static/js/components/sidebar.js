export function loadSidebar(container) {
    container.innerHTML = `
  <div class="sidebar-root">
     <aside class="side-panel">
        <div class="side-panel-header">
           <button class="side-panel-close-btn">&larr;</button>
           <div class="side-panel-title">Моя музыка</div>
        </div>

        <div class="track-collection-btn">
           <a href="/collection/tracks">
           <button class="side-panel-btn track-collection-href">Понравившиеся песни</button>
           </a>
        </div>

        <div class="album-collection-btn">
           <a href="/collection/albums">
           <button class="side-panel-btn album-collection-href">Понравившиеся альбомы</button>
           </a>
        </div>

        <div class="genres-btn">
           <a href="/genre">
           <button class="side-panel-btn genres-href">Жанры</button>
           </a>
        </div>
     </aside>
     <div class="side-panel-overlay"></div>
  </div>`;
    return container.querySelector(".sidebar-root");
}