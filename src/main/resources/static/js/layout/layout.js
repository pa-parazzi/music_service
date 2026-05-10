import {loadHeader} from "../components/header.js";
import {initSearchForm} from "../module/search.js";
import {loadSidebar} from "../components/sidebar.js";
import {loadAudioPlayer} from "../components/audioPlayer.js";
import {initSidebar} from "../module/sidebar.js";
import {initPlayer} from "../module/player.js";
import {initUser, showAuthButtons} from "../user/initUser.js";

export async function renderLayout(layoutType) {
    const header = document.getElementById("header");
    const sideBar = document.getElementById("sidebar");
    const player = document.getElementById("audio-player");

    switch(layoutType){
        case "main":
            await renderMainLayout(header, sideBar, player);
            break;
        case "auth":
            await renderAuthLayout(header, sideBar, player);
            break;
        default:
            clearLayout(header, sideBar, player);
    }
}

async function renderMainLayout(header, sideBar, player){
    const headerContainer = loadHeader(header);
    initSearchForm(headerContainer.querySelector(".search-form"));
    const sideBarRootContainer = loadSidebar(sideBar);
    initSidebar(headerContainer, sideBarRootContainer);
    const audioPlayerRootContainer = loadAudioPlayer(player);
    initPlayer(audioPlayerRootContainer);
    await initUser(headerContainer);
}

async function renderAuthLayout(header, sideBar, player){
    const headerContainer = loadHeader(header);
    initSearchForm(headerContainer.querySelector(".search-form"));
    const sideBarRootContainer = loadSidebar(sideBar);
    initSidebar(headerContainer, sideBarRootContainer);
    const authButtonsContainer = headerContainer.querySelector(".auth-buttons");
    const userProfileContainer = headerContainer.querySelector(".user-profile");
    showAuthButtons(authButtonsContainer, userProfileContainer)
    player.innerHTML = "";
}

function clearLayout(header, sideBar, player){
    header.innerHTML = "";
    sideBar.innerHTML = "";
    player.innerHTML = "";
}