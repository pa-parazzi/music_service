export function initUserAvatarButton(avatarBtn, dropDown){
    avatarBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        dropDown.classList.toggle("open", true);
    });

    document.addEventListener("click", () => {
        dropDown.classList.toggle("open", false);
    });
}