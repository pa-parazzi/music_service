import {loadHeader} from "../components/header.js";
import {initSearchForm} from "../module/search.js";
import {loadSidebar} from "../components/sidebar.js";
import {loadAudioPlayer} from "../components/audioPlayer.js";
import {initSidebar} from "../module/sidebar.js";
import {initPlayer} from "../module/player.js";
import {initUser} from "../user/initUser.js";

export async function renderLayout(layoutType) {
    const header = document.getElementById("header");
    const sideBar = document.getElementById("sidebar");
    const player = document.getElementById("audio-player");

    if (layoutType === "main") {
        const headerContainer = loadHeader(header);
        initSearchForm(headerContainer.querySelector(".search-form"));
        const sideBarRootContainer = loadSidebar(sideBar);
        initSidebar(headerContainer, sideBarRootContainer);
        const audioPlayerRootContainer = loadAudioPlayer(player);
        initPlayer(audioPlayerRootContainer);
        await initUser(headerContainer);
        return;
    }

    header.innerHTML = "";
    sideBar.innerHTML = "";
    player.innerHTML = "";
}