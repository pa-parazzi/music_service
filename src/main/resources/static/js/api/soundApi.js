export async function getSoundListByAlbumId(id){
    const soundListResponse = await fetch(`/api/sound/album/${id}`);
    const soundListJson = await soundListResponse.json();
    return soundListJson.soundList;
}

export async function getSoundListByArtistId(id){
    const soundListResponse = await fetch(`/api/sound/artist/${id}`);
    const soundListJson = await soundListResponse.json();
    return  soundListJson.soundList;
}

export async function getSoundById(id){
    const soundResponse = await fetch(`/api/sound/${id}`);
    return await soundResponse.json();
}