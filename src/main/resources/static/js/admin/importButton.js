import {importMusicDataByGenre} from "./adminApi.js";

export function initImportBtn(importBtn, importStatus, genreSelect){
    const clickBtnHandler = async () => {
        const genreName = genreSelect.value;

        importStatus.textContent = "Импорт начался...";
        try {
            const response = await importMusicDataByGenre(genreName);

            if (response.ok) {
                importStatus.textContent = "Импорт успешно завершён";
            } else {
                const errText = await response.text();
                importStatus.textContent = "Ошибка при импорте: " + (errText || response.status);
            }
        } catch (error) {
            importStatus.textContent = "Не удалось запустить импорт";
        }
    };
    importBtn.addEventListener("click", clickBtnHandler);
    return function remove(){
        importBtn.removeEventListener("click", clickBtnHandler);
    }
}