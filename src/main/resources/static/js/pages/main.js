import {initSearchForm} from "../module/search.js";
import {initSidebar} from "../module/sidebar.js";

async function initMainPage(){
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initMainPage();
});