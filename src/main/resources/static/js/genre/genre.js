import {loadProfile} from "../user/loadProfile.js";
import {loadGenres} from "./loadGenres.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile();
    await loadGenres();
});