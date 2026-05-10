import {logout} from "../auth/logout.js";
import {loadUserProfile} from "./userApi.js";
import {renderUserProfile} from "./userView.js";
import {initUserAvatarButton} from "./initAvatarButton.js";

export async function initUser(headerContainer) {

    const authButtonsContainer = headerContainer.querySelector(".auth-buttons");
    const userProfileContainer = headerContainer.querySelector(".user-profile");

    const user = await loadUserProfile();

    if(!user) {
        showAuthButtons(authButtonsContainer, userProfileContainer);
        return;
    }

    showUserProfile(authButtonsContainer, userProfileContainer, user);

    const avatarBtn = userProfileContainer.querySelector(".avatar-btn");
    const dropDown = userProfileContainer.querySelector(".profile-drop-down");
    const logoutBtn = userProfileContainer.querySelector(".logout-btn");

    initUserAvatarButton(avatarBtn, dropDown);
    logoutBtn.addEventListener("click", logout);
}

function showUserProfile(authButtonsContainer, userProfileContainer, user){
    authButtonsContainer.style.display = "none";
    userProfileContainer.style.display = "flex";
    renderUserProfile(userProfileContainer, user);
}

export function showAuthButtons(authButtonsContainer, userProfileContainer){
    authButtonsContainer.style.display = "flex";
    userProfileContainer.style.display = "none";
}