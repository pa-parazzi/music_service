import {loadProfile} from "../user/loadProfile.js";
import {loadTrackCollection} from "./loadTrackCollection.js";

document.addEventListener("DOMContentLoaded", async () => {
    const user = await loadProfile();
    await loadTrackCollection(user);
});