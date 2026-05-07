export function renderSearchOverviewLayout(container, searchFragment){
    container.innerHTML = `
    <div class="page-section">
          <section class="sounds-section">
            <div class="section-heading">
                <a href="/search/${searchFragment}/tracks" class="title-link">
                    <h2>Треки</h2>
                </a>
                <a href="/search/${searchFragment}/tracks" class="show-all-btn">Показать все</a>
            </div>
            <div class="sounds"></div>
          </section>
          
          <section class="albums-section">
            <div class="section-heading">
                  <a href="/search/${searchFragment}/albums" class="title-link">
                     <h2>Альбомы</h2>
                  </a>
                <a href="/search/${searchFragment}/albums" class="show-all-btn">Показать все</a>
            </div>
            <div class="albums"></div>
          </section>
          
          <section class="artists-section">
            <div class="section-heading">
                  <a href="/search/${searchFragment}/artists" class="title-link">
                     <h2>Исполнители</h2>
                  </a>
                <a href="/search/${searchFragment}/artists" class="show-all-btn">Показать все</a>
            </div>
            <div class="artists"></div>
          </section>
    </div>

    <div class="not-found"></div>`;
}