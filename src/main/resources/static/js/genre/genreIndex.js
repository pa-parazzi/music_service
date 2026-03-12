import {loadProfile} from "../user/loadProfile.js";
import {loadGenreData} from "./loadGenreData.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadGenreData();
});