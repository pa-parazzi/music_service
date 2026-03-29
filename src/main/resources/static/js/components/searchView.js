export function renderSearchGeneralResult(container){
    container.innerHTML = `
    <h2 class="tracks-title" id="tracks-title"></h2>
    <div class="tracks" id="tracks"></div>

    <h2 class="albums-title" id="albums-title"></h2>
    <div class="albums" id="albums"></div>

    <h2 class="artists-title" id="artists-title"></h2>
    <div class="artists" id="artists"></div>

    <div class="empty-result" id="empty-result"></div>`;
}

export function renderSearchArtistsExtendedResult(container){
    container.innerHTML = `
            <div class="extended-search-result">
                 <h2 class="search-title" id="search-title"></h2>
                 <div class="artists"></div>
            </div>`;
}

export function renderSearchAlbumsExtendedResult(container){
    container.innerHTML = `
            <div class="extended-search-result">
                 <h2 class="search-title" id="search-title"></h2>
                 <div class="albums"></div>
            </div>`;
}

export function renderSearchTracksExtendedResult(container){
    container.innerHTML = `
            <div class="extended-search-result">
                 <h2 class="search-title" id="search-title"></h2>
                 <div class="tracks"></div>
            </div>`;
}