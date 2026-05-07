import {escapeHtml} from "../utils/util.js";

export function renderGenresPage(container, genres){
    container.innerHTML = genres.map((genre) => `
        <div class="genre-card">
           <div class="genre-name">${escapeHtml(genre.name)}</div>
              <div class="genre-cover">
                 <a href="/genre/${genre.id}" class="genre-link">
                   <img src="/image/genre/${genre.imageName}"
                   alt="${escapeHtml(genre.name)}" class="genre-image">
                 </a>
              </div>
        </div>
    `).join('');
}

export function renderGenrePageContainer(container){
    container.innerHTML = `<div class="genre-page"></div>`;
    return container.querySelector(".genre-page");
}

export function renderGenresContainer(container){
    container.innerHTML = `<div class="genres"></div>`;
    return container.querySelector(".genres");
}

export function renderGenresWithLimit(container, genres, visibleLimit){
    const visibleGenres = genres.slice(0, visibleLimit);
    container.innerHTML = visibleGenres.map((genre) =>`
      <div class="genre-card">
          <div class="genre-card-cover">
             <a href="/genre/${genre.id}" class="genre-card-link">
               <img src="/image/genre/${genre.imageName}" alt="${escapeHtml(genre.name)}" class="genre-card-image">
             </a>
          </div>
          <div class="genre-card-meta">
             <a href="/genre/${genre.id}" class="genre-card-name-link">
               <div class="genre-card-name">${escapeHtml(genre.name)}</div>
             </a>
          </div>
      </div>`).join('');
}

export function renderGenreContent(genreContainer, genreId){
    genreContainer.innerHTML = `
    <div class="page-section">
          <section class="sounds-section">
            <div class="section-heading">
                  <a class="title-link" href="/genre/${genreId}/tracks">
                     <h2>Треки</h2>
                  </a>
                 <a href="/genre/${genreId}/tracks" class="show-all-btn">Показать все</a>
            </div>
            <div class="sounds"></div>
          </section>
          <section class="albums-section">
            <div class="section-heading">
                  <a href="/genre/${genreId}/albums" class="title-link">
                     <h2>Альбомы</h2>
                  </a>
                <a href="/genre/${genreId}/albums" class="show-all-btn">Показать все</a>
            </div>
            <div class="albums"></div>
          </section>
    </div>`;
    return genreContainer.querySelector(".page-section");
}