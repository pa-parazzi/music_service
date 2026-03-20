export async function search(fragment){
    const response = await fetch(`/api/search/${fragment}`,{
        method: "GET"
    });
    return await response.json();
}

export async function getAllFoundTracks(fragment){
    const response = await fetch(`/api/search/${fragment}/tracks`, {
        method: "GET"
    });
    return await response.json();
}

export async function getAllFoundAlbums(fragment){
    const response = await fetch(`/api/search/${fragment}/albums`, {
        method: "GET"
    });
    return await response.json();
}

export async function getAllFoundArtists(fragment){
    const response = await fetch(`/api/search/${fragment}/artists`, {
        method: "GET"
    });
    return await response.json();
}