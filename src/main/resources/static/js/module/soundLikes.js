import {createSoundLike, deleteSoundLike} from "../api/soundLikesApi.js";

export function initSoundLikeBySoundId(likeSoundStatus, likeBtn, soundId) {
    if (likeSoundStatus && likeSoundStatus.likeStatus === true) {
        likeBtn.classList.add("liked");
    }
    const clickLikeHandler = async (e) => {
        e.stopPropagation();
        if (likeBtn.classList.contains("liked")) {
            await deleteSoundLike(soundId);
            likeBtn.classList.toggle("liked", false);
        } else if (!likeBtn.classList.contains("liked")) {
            await createSoundLike(soundId);
            likeBtn.classList.toggle("liked", true);
        }
    }
    likeBtn.addEventListener("click", clickLikeHandler);
    return function remove(){
        likeBtn.removeEventListener("click", clickLikeHandler);
    }
}