import {apiFetch} from "../user/api.js";

export function upload(){
    document.addEventListener("DOMContentLoaded", async () => {
        const importBtn = document.getElementById("importBtn");
        const statusDiv = document.getElementById("importStatus");

        importBtn.addEventListener("click", async () => {
            statusDiv.textContent = "Импорт начался...";

            try {
                const response = await apiFetch('/admin/upload', {
                    method: "POST",
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
    });
}