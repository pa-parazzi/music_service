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
    let observer;
    let active = true;

    function isScrollable() {
        return document.documentElement.scrollHeight > window.innerHeight;
    }

    async function loadWhileNotScrollable() {
        while (active && hasNextFn() && !isScrollable()) {
            await loadFn();
        }
    }

    function initObserver() {
        observer = new IntersectionObserver(async (entries) => {
            if (!active) return;

            const entry = entries[0];
            if (!entry.isIntersecting) return;
            if (!hasNextFn() || isLoadingFn()) return;

            await loadFn();
        });

        observer.observe(anchor);
    }

    async function init() {
        await loadWhileNotScrollable();
        initObserver();
    }

    function destroy() {
        active = false;
        observer?.disconnect();
    }

    return {
        init,
        destroy
    };
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