import {initAlbumPage} from "../pages/album.js";
import {initLoginPage} from "../pages/login.js";
import {renderLayout} from "../layout/layout.js";
import {initArtistPage} from "../pages/artist.js";
import {initAlbumCollectionPage} from "../pages/collectionAlbums.js";
import {initTrackCollectionPage} from "../pages/collectionTracks.js";
import {initGenrePage} from "../pages/genre.js";
import {initGenreOverviewPage} from "../pages/genreOverview.js";
import {initGenreContentPage} from "../pages/genreContent.js";
import {initAlbumReleasesPage} from "../pages/albumReleases.js";
import {initMainPage} from "../pages/main.js";
import {initRegistrationPage} from "../pages/registration.js";
import {initSearchOverviewPage} from "../pages/searchOverview.js";
import {initSearchContentPage} from "../pages/searchContent.js";
import {initSoundPage} from "../pages/sound.js";
import {initAdminPage} from "../admin/adminPage.js";

const routes = [
    { path: "/admin", page: initAdminPage, layout: "admin" },
    { path: "/auth/login", page: initLoginPage, layout: "auth" },
    { path: "/auth/registration", page: initRegistrationPage, layout: "auth" },
    { path: "/main", page: initMainPage, layout: "main" },
    { path: "/search/:fragment", page: initSearchOverviewPage, layout: "main" },
    { path: "/search/:fragment/:type", page: initSearchContentPage, layout: "main" },
    { path: "/album/releases", page: initAlbumReleasesPage, layout: "main" },
    { path: "/album/:id", page: initAlbumPage, layout: "main" },
    { path: "/artist/:id", page: initArtistPage, layout: "main" },
    { path: "/sound/:id", page: initSoundPage, layout: "main" },
    { path: "/collection/albums", page: initAlbumCollectionPage, layout: "main" },
    { path: "/collection/tracks", page: initTrackCollectionPage, layout: "main" },
    { path: "/genre", page: initGenrePage, layout: "main" },
    { path: "/genre/:id", page: initGenreOverviewPage, layout: "main" },
    { path: "/genre/:id/:type", page: initGenreContentPage, layout: "main" }
];

function matchRoute(pathname) {
    for (const route of routes) {

        const paramNames = [];

        const regexPath = route.path.replace(/:([^/]+)/g, (_, key) => {
            paramNames.push(key);
            return "([^/]+)";
        });

        const regex = new RegExp(`^${regexPath}$`);
        const match = pathname.match(regex);

        if (!match) continue;

        const params = {};
        paramNames.forEach((name, i) => {
            params[name] = match[i + 1];
        });

        return {
            page: route.page,
            params,
            layout: route.layout
        };
    }

    return null;
}

let currentCleanup = null;
let currentLayout = null;

async function navigate(pageFn, params, layout) {

    if (currentCleanup) {
        currentCleanup();
        currentCleanup = null;
    }

    if (currentLayout !== layout) {
        await renderLayout(layout);
        currentLayout = layout;
    }

    currentCleanup = await pageFn(params);
}

async function handleRoute() {
    const match = matchRoute(location.pathname);

    if (!match) {
        document.getElementById("app").innerHTML = "<h2>404</h2>";
        return;
    }

    await navigate(match.page, match.params, match.layout);
}

export function initRouter() {

    document.addEventListener("click", (e) => {

        const link = e.target.closest("a");
        if (!link) return;

        // только левый клик
        if (e.button !== 0) return;

        // если зажаты модификаторы — пусть браузер работает нативно
        if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;

        // target="_blank"
        if (link.target === "_blank") return;

        // download
        if (link.hasAttribute("download")) return;

        const url = new URL(link.href);

        // внешний домен
        if (url.origin !== location.origin) return;

        // якорь
        if (url.hash && url.pathname === location.pathname) return;

        e.preventDefault();

        history.pushState({}, "", url.pathname);
        handleRoute();
    });

    window.addEventListener("popstate", handleRoute);

    handleRoute();
}