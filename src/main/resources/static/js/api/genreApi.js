import {apiFetch} from "../user/api.js";

export async function getGenres(){
    const response = await apiFetch('/api/genre', {
        method: "GET"
    });
    if (!response.ok){
        throw new Error("Ошибка загрузки списка жанров");
    }
    return  await response.json();
}

