export function renderGenreDetails(genreContainer, genreId){
    genreContainer.innerHTML = `
        <div class="genre-content">
          <section class="albums-section">
            <h2 class="section-title">Альбомы</h2>
            <div class="albums-row">
                <div class="albums-${genreId}"></div>
                <a href="/genre/${genreId}/albums" class="show-all-btn">
                    Показать все
                </a>
            </div>
          </section>
          <section class="artists-section">
            <h2 class="section-title">Исполнители</h2>
            <div class="artists-container artists-${genreId}"></div>
          </section>
    </div>`;
}