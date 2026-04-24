import {paginationStateOfAlbums, paginationStateOfArtists, paginationStateOfSounds} from "../store/paginationState.js";

export function escapeHtml(str = '') {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

export function formatTime(seconds) {
    if (isNaN(seconds) || seconds === Infinity) return "0:00";

    seconds = Math.floor(seconds); // Убираем дроби

    const m = Math.floor(seconds / 60);
    const s = seconds % 60;

    return `${m}:${String(s).padStart(2, "0")}`;
}

export function initInfiniteScroll({ loadFn, hasNextFn, isLoadingFn, anchor }) {
    const observer = new IntersectionObserver(async (entries) => {
        if (entries[0].isIntersecting) {
            if (!hasNextFn() || isLoadingFn()) return;
            await loadFn();
        }
    });
    observer.observe(anchor);
    return observer;
}

export function resetPaginationState(){
    paginationStateOfAlbums.currentPage = 0;
    paginationStateOfAlbums.size = 6;
    paginationStateOfAlbums.isLoading = false;
    paginationStateOfAlbums.hasNext = true;
    paginationStateOfAlbums.albums = [];

    paginationStateOfSounds.currentPage = 0;
    paginationStateOfSounds.size = 6;
    paginationStateOfSounds.isLoading = false;
    paginationStateOfSounds.hasNext = true;
    paginationStateOfSounds.sounds = [];

    paginationStateOfArtists.currentPage = 0;
    paginationStateOfArtists.size = 6;
    paginationStateOfArtists.isLoading = false;
    paginationStateOfArtists.hasNext = true;
    paginationStateOfArtists.artists = [];
}