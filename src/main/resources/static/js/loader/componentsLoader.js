async function loadComponents() {

    const elements = document.querySelectorAll("[data-component]");

    await Promise.all([...elements].map(async el => {
        const name = el.dataset.component;
        const res = await fetch(`/html/components/${name}.html`);
        el.outerHTML = await res.text();
    }));

    document.dispatchEvent(new Event("componentsLoaded"));
}

document.addEventListener("DOMContentLoaded", loadComponents);