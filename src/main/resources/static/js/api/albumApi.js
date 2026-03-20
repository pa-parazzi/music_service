export async function getAlbumById(id){
    const response = await fetch(`/api/album/${id}`);
    if (!response.ok) throw new Error("Ошибка загрузки альбома");
    return await response.json();
}