import {loadProfile} from "../user/loadProfile.js";
import {loadArtist} from "./loadArtist.js";

document.addEventListener("DOMContentLoaded", async () => {
    const user = await loadProfile();
    await loadArtist(user);
});