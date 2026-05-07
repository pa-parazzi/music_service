import {createSoundLike, deleteSoundLike} from "../api/soundLikesApi.js";

export function initSoundLikeBySoundId(jwt, likeSoundStatus, likeBtn, soundId) {
    if (likeSoundStatus.likeStatus === true) {
        likeBtn.classList.add("liked");
    }
    const clickLikeHandler = async (e) => {
        e.stopPropagation();
        if (likeBtn.classList.contains("liked")) {
            await deleteSoundLike(jwt, soundId);
            likeBtn.classList.toggle("liked", false);
        } else if (!likeBtn.classList.contains("liked")) {
            await createSoundLike(jwt, soundId);
            likeBtn.classList.toggle("liked", true);
        }
    }
    likeBtn.addEventListener("click", clickLikeHandler);
    return function remove(){
        likeBtn.removeEventListener("click", clickLikeHandler);
    }
}