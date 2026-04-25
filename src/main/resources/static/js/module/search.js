export function initSearchForm(searchForm){
    searchForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const fragment = document.getElementById("search-input").value.trim();
        if (!fragment) {
            return;
        }
        window.location.href = `/search/${encodeURIComponent(fragment)}`;
    });
}