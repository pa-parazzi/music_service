import {apiFetch} from "../user/userApi.js";

export async function importMusicDataByGenre(genreName){
    return await apiFetch("/api/admin/import?genreName=" + encodeURIComponent(genreName), {
        method: "POST"
    });
}