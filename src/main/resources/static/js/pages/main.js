import {initSearchForm} from "../module/search.js";
import {initSidebar} from "../module/sidebar.js";
import {initPlayer} from "../module/player.js";

async function initMainPage(){
    initPlayer();
    const searchForm = document.getElementById("search-form");
    initSearchForm(searchForm);
}

document.addEventListener("componentsLoaded", async ()=> {
    initSidebar();
    await initMainPage();
});