import {loadProfile} from "../user/loadProfile.js";
import {loadAlbum} from "./loadAlbum.js";

document.addEventListener("DOMContentLoaded", async () => {
    const user = await loadProfile();
    await loadAlbum(user);
});