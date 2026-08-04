document.addEventListener("submit", (event) => {
    const form = event.target.closest("form[data-confirm]");
    if (form && !window.confirm(form.dataset.confirm)) {
        event.preventDefault();
    }
});

document.addEventListener("click", (event) => {
    const link = event.target.closest("a[data-scroll-target]");
    if (!link) {
        return;
    }

    const target = document.getElementById(link.dataset.scrollTarget);
    if (!target) {
        return;
    }

    event.preventDefault();
    target.scrollIntoView({ behavior: "smooth", block: "start" });
    target.focus({ preventScroll: true });
    target.classList.remove("is-scroll-highlighted");
    window.requestAnimationFrame(() => target.classList.add("is-scroll-highlighted"));
    window.history.replaceState(null, "", `#${target.id}`);
});
