import {loadProfile} from "../user/loadProfile.js";
import {loadTrackCollection} from "./loadTrackCollection.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadTrackCollection();
});