import {initAlbumPage, initAlbumReleasesPage} from "../pages/album.js";

export async function router() {
    const { pathname } = window.location;

    if (pathname === "/album/releases") {
        await initAlbumReleasesPage();
        return;
    }

    const albumMatch = pathname.match(/^\/album\/(\d+)$/);
    if (albumMatch) {
        await initAlbumPage(Number(albumMatch[1]));
        return;
    }
}