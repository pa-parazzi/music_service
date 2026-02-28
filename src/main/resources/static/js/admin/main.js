import {loadProfile} from "../user/loadProfile.js";
import {upload} from "./upload.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadProfile('/admin/main');
    upload();
});