import {loadProfile} from "../user/loadProfile.js";
import {loadAlbumCollection} from "./loadAlbumCollection.js";

document.addEventListener("DOMContentLoaded", async () => {
    const user = await loadProfile();
    await loadAlbumCollection(user);
});