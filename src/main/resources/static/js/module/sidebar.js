export function initSidebar(headerContainer, sideBarRootContainer){

    const sidebar = sideBarRootContainer.querySelector(".side-panel");
    const openBtn = headerContainer.querySelector(".sidebar-open-btn");
    const closeBtn = sideBarRootContainer.querySelector(".side-panel-close-btn");
    const overlay = sideBarRootContainer.querySelector(".side-panel-overlay");

    function openSidebar(){
        sidebar.classList.add("open");
        overlay.classList.add("active");
    }

    function closeSidebar(){
        sidebar.classList.remove("open");
        overlay.classList.remove("active");
    }

    document.addEventListener("keydown", (e) => {
        if(e.key === "Escape"){
            closeSidebar();
        }
    });

    sideBarRootContainer.addEventListener("click", (e) => {
        if(e.target.closest(".side-panel-btn")){
            closeSidebar();
        }
    });

    openBtn.addEventListener("click", openSidebar);
    closeBtn.addEventListener("click", closeSidebar);
    overlay.addEventListener("click", closeSidebar);

}
