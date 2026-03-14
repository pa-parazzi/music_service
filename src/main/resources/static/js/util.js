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