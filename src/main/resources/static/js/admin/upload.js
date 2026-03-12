import {apiFetch} from "../user/api.js";

export async function upload() {
    const importBtn = document.getElementById("importBtn");
    const statusDiv = document.getElementById("importStatus");
    const genreSelect = document.getElementById('genreSelect');

    const genresResponse = await apiFetch('/api/genre', {
        method: "GET"
    });

    const genresJson = await genresResponse.json();
    const genres = genresJson.genres;

    genreSelect.innerHTML = "";
    genres.forEach(genre => {
        const option = document.createElement('option');
        option.value = genre.name;
        option.textContent = genre.name;
        genreSelect.appendChild(option);
    });

    importBtn.addEventListener("click", async () => {

        const genre = genreSelect.value;
        statusDiv.textContent = "Импорт начался...";

        try {
            const response = await apiFetch("/admin/upload?genreName=" + encodeURIComponent(genre), {
                method: "POST"
            });

            if (response.ok) {
                statusDiv.textContent = "Импорт успешно завершён!";
            } else {
                const errText = await response.text();
                statusDiv.textContent = "Ошибка при импорте: " + (errText || response.status);
            }
        } catch (error) {
            console.error("Ошибка при запуске импорта:", error);
            statusDiv.textContent = "Не удалось запустить импорт.";
        }
    });
}