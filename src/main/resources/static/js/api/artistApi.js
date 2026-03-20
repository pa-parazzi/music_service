export async function getArtistById(id){
    const response = await fetch(`/api/artist/${id}`);
    if (!response.ok){
        throw new Error("Ошибка загрузки исполнителя с id: " + id)
    }
    return  await response.json();
}