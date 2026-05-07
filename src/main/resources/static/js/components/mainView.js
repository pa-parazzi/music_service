export function renderMainPageLayout(container){
    container.innerHTML = `
    <section class="album-releases-section">
        <div class="section-heading">
            <a href="/album/releases" class="title-link">
                <h2>Новые релизы</h2>
            </a>
            <a href="/album/releases" class="show-all-btn">Показать все</a>
        </div>
        <div class="album-rows"></div>
    </section>

    <section class="genres-cards-section">
        <div class="section-heading">
            <a href="/genre" class="title-link">
                <h2>Жанры</h2>
            </a>
            <a href="/genre" class="show-all-btn">Показать все</a>
        </div>
        <div class="genre-rows"></div>
    </section>`;
}