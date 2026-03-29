export async function initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId){
    if (likeSoundStatus.status === true) {
        likeBtn.classList.add("liked");
    }
    likeBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (likeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-sounds/${soundId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            likeBtn.classList.toggle("liked", false);
        } else if (!likeBtn.classList.contains("liked")) {
            await fetch(`/api/liked-sounds/${soundId}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${jwt}`
                }
            });
            likeBtn.classList.toggle("liked", true);
        }
    });
}