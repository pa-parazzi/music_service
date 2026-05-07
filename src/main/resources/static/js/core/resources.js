const activeStyles = new Set();

export function loadCss(href) {
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = href;
    document.head.appendChild(link);
    activeStyles.add(link);
    return link;
}

export function unloadCss(link){
    if(activeStyles.has(link)){
        link.remove();
        activeStyles.delete(link);
    }
}