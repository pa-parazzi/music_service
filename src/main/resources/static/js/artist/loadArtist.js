import{escapeHtml} from "../util.js";
import{initSounds} from "../sound/initSounds.js";
import{audioListener} from "../audio/audioListener.js";
import {loadSoundLikes} from "../sound/loadSoundLikes.js";

const player = document.getElementById('player');
const playBtn = document.getElementById('play-btn');
const nextBtn = document.getElementById('next-btn');
const prevBtn = document.getElementById('prev-btn');
const artistName = document.getElementById('artist-name');
const tracksContainer = document.getElementById('tracklist');

export async function loadArtist() {
    const id = window.location.pathname.split('/').pop();

    const response = await fetch(`/api/artist/${id}`);
    if (!response.ok){
        console.log("Ошибка загрузки исполнителя");
        return;
    }

    const artist = await response.json();

    artistName.textContent = artist.name;
    artistName.alt = escapeHtml(artist.name);

    const soundListResponse = await fetch(`/api/sound/artist/${id}`);
    const soundListJson = await soundListResponse.json();
    const soundList = soundListJson.soundList;

    const likedSounds = await loadSoundLikes();

    await initSounds({
        trackListContainer: tracksContainer,
        soundList: soundList,
        likedSounds: likedSounds
    });

    const playerState = {
        currentTrackIndex: 0,
        soundList: soundList
    }

    audioListener(playerState, player, playBtn, nextBtn, prevBtn);
}
