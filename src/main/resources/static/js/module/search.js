export function initSearchForm(searchForm){
    const input = searchForm.querySelector(".search-input");

    searchForm.addEventListener("submit", (e) => {
        e.preventDefault();
        const fragment = input.value.trim();
        if (!fragment) return;
        history.pushState({}, "", `/search/${encodeURIComponent(fragment)}`);
        window.dispatchEvent(new PopStateEvent("popstate"));
    });
}