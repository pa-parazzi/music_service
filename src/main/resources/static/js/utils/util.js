import {paginationState} from "../store/PaginationState.js";

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
    paginationState.currentPage = 0;
    paginationState.isLoading = false;
    paginationState.hasNext = true;
    paginationState.artists = [];
    paginationState.albums = [];
    paginationState.tracks = [];
}