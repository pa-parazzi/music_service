import {logout} from "./logout.js";
import {apiFetch} from "./api.js";
import {clearAuth} from "./auth.js";

export async function loadProfile() {

    const nav = document.getElementById("auth-buttons");
    const userInfoDiv = document.getElementById("userInfo");

    try {
        const response = await apiFetch('/user/profile', {
            method: "GET"
        });

        if (response.ok) {
            const user = await response.json();

            nav.style.display = "none";
            userInfoDiv.innerHTML = `
            <div class="profile-menu">
              <button class="avatar-btn" id="avatar-btn">
                   <img src="${user.avatar?.url}" alt="avatar">
              </button>
              
              <div class="profile-drop-down" id="profile-drop-down">
                   <div class="profile-name">${user.username}</div>
                   <button id="logoutBtn" class="logout-btn">Выйти</button>             
              </div>
            </div>
            `;

            const avatarBtn = document.getElementById("avatar-btn");
            const dropDown = document.getElementById("profile-drop-down");

            avatarBtn.addEventListener("click", (e) => {
                e.stopPropagation();
                dropDown.classList.toggle("open", true);
            });

            document.addEventListener("click", ()=> {
                dropDown.classList.toggle("open", false);
            });

            const logoutBtn = document.getElementById("logoutBtn");
            logoutBtn.addEventListener("click", logout);

            userInfoDiv.style.display = "block";
            return user;
        } else {
            clearAuth();
            userInfoDiv.style.display = "none";
            nav.style.display = "flex";
            console.warn("Сессия истекла или токен недействителен");
        }
    } catch (error) {
        console.error("Ошибка загрузки профиля:", error);
        userInfoDiv.style.display = "none";
        nav.style.display = "flex";
    }
}
document.addEventListener("componentsLoaded", async () => {
    await loadProfile();
});