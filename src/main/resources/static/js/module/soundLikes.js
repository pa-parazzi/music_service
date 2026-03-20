export async function initSoundLikes(likedSounds, soundLikeButtons, jwt){
    const likedSoundsIds = new Set(likedSounds.ids);
    soundLikeButtons.forEach(likeBtn => {
        const trackId = Number(likeBtn.dataset.trackId);
        if (likedSoundsIds.has(trackId)) {
            likeBtn.classList.add("liked");
        }
        initSoundLikeButton(jwt, likeBtn, trackId);
    });
}

export async function initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId){
    if (likeSoundStatus.status === true) {
        likeBtn.classList.add("liked");
    }
    await initSoundLikeButton(jwt, likeBtn, soundId);
}

export async function initSoundLikeButton(jwt, likeBtn, soundId){
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