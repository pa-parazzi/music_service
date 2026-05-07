export function loadAdminLayout(container){
    container.innerHTML = `
    <section class="import-section">
        <h2>Импорт музыкальных данных</h2>
        <label for="genre-select">Выберите жанр:</label>
        <select name="genre-select" class="genre-select"></select>
        <button class="import-button">Загрузить</button>
        <div class="import-status"></div>
    </section>`;
}