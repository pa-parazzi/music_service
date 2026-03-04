import {loadProfile} from "../user/loadProfile.js";
import {loadArtist} from "./loadArtist.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadArtist();
});