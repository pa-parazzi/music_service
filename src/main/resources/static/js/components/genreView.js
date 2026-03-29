export function renderGeneralGenreContent(genreContainer, genreId){
    genreContainer.innerHTML = `
    <div class="general-genre-content">
          <section class="tracks-section">
            <div class="header-tracks-row">
                 <h2 class="section-title">Треки</h2>
                 <a href="/genre/${genreId}/tracks" class="show-all-btn">Показать все</a>
            </div>
            <div class="tracks-general-container">
                <div class="tracks"></div>
            </div>       
          </section>
          <section class="albums-section">
            <div class="header-albums-row">
                <h2 class="section-title">Альбомы</h2>
                <a href="/genre/${genreId}/albums" class="show-all-btn">Показать все</a>
            </div>
            <div class="albums-general-container">
                <div class="albums"></div>
            </div>
          </section>
    </div>`;
}

export function renderTracksContainerForGenre(container, genreName){
    container.innerHTML = `
      <div class="extended-genre-content">
         <h2 class="genre-name-header">${genreName}</h2>
         <div class="tracks-container">
             <div class="tracks"></div>
         </div>
      </div>
    `;
}

export function renderAlbumsContainerForGenre(container, genreName){
    container.innerHTML = `
       <div class="extended-genre-content">
          <h2 class="genre-name-header">${genreName}</h2>
          <div class="albums-container">
              <div class="albums"></div>
          </div>
       </div>
    `;
}