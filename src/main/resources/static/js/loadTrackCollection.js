import {initTrackList} from "./trackList.js";

document.addEventListener("userLoaded", ()=>{
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    loadTrackCollection();
});

async function loadTrackCollection(){
    const trackCollection = document.getElementById("track-collection");
    const userId = window.currentUser.id;

    const likeResponses = await fetch('/like/get/soundLikes', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({userId})
    });

    if(!likeResponses.ok){
        return;
    }

    const likeList = await likeResponses.json();

    const trackCollectionResponse = await fetch('/collection/tracks', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(likeList)
    });

    const collectionData = await trackCollectionResponse.json();

    await initTrackList({
        trackList: trackCollection,
        object: collectionData
    });
}


