export function initSidebar(){

    const sidebar = document.getElementById("sidebar");
    const openBtn = document.getElementById("sidebar-open");
    const closeBtn = document.getElementById("sidebar-close");
    const overlay = document.getElementById("sidebar-overlay");

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

    openBtn.addEventListener("click", openSidebar);
    closeBtn.addEventListener("click", closeSidebar);
    overlay.addEventListener("click", closeSidebar);

}
