import {logout} from "../auth/logout.js";
import {loadUserProfile} from "./userApi.js";
import {renderUserProfile} from "./userView.js";
import {initUserAvatarButton} from "./initAvatarButton.js";

export async function initUser(headerContainer) {

    const authButtonsContainer = headerContainer.querySelector(".auth-buttons");
    const userProfileContainer = headerContainer.querySelector(".user-profile");

    const user = await loadUserProfile(userProfileContainer, authButtonsContainer);

    if(!user) return;

    authButtonsContainer.style.display = "none";
    renderUserProfile(userProfileContainer, user);

    const avatarBtn = userProfileContainer.querySelector(".avatar-btn");
    const dropDown = userProfileContainer.querySelector(".profile-drop-down");

    initUserAvatarButton(avatarBtn, dropDown);

    const logoutBtn = userProfileContainer.querySelector(".logout-btn");
    logoutBtn.addEventListener("click", logout);
    userProfileContainer.style.display = "block";
}