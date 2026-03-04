import {loadProfile} from "../user/loadProfile.js";
import {loadAlbumCollection} from "./loadAlbumCollection.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadAlbumCollection();
});