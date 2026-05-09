export async function getArtistById(id){
    const response = await fetch(`/api/artist/${id}`);
    if(!response.ok) throw new Error("Failed to load artist");
    return await response.json();
}