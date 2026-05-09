import {apiFetch} from "../api/httpClient.js";

export async function importMusicDataByGenre(genreName){
    return await apiFetch("/api/admin/import?genreName=" + encodeURIComponent(genreName), {
        method: "POST"
    });
}