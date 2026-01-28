import{escapeHtml} from "../util.js";
import{initSoundListWithLikes} from "../soundListWithLikes.js";
import{audioListener} from "../audio/audioListener.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');
const artistName = document.getElementById('artist-name');
const artistTrackList = document.getElementById('tracklist');

async function loadArtist() {
    const id = window.location.pathname.split('/').pop();

    try {
        const response = await fetch(`/api/artist/${id}`);
        if (!response.ok) throw new Error("Ошибка загрузки");

        const artist = await response.json();


        artistName.textContent = artist.name;
        artistName.alt = escapeHtml(artist.name);

        const soundListResponse = await fetch(`/api/sound/artist/${id}`);
        const soundList = await soundListResponse.json();

        await initSoundListWithLikes({
            trackList: artistTrackList,
            soundList: soundList
        });

        const playerState = {
            currentTrackIndex: 0,
            soundList: soundList
        }

        audioListener(playerState, player, playBtn, nextBtn, prevBtn);

    } catch (err) {
        console.error("Ошибка загрузки исполнителя", err);
    }
}

(async function initUser(){
    await window.loadUser;
    if(!window.currentUser){
        console.log("Пользователь не авторизирован");
        return;
    }
    await loadArtist();
})();
