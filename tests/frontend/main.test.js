beforeAll(async () => {
  await import("../../src/main/resources/static/scripts/main.js");
});

beforeEach(() => {
  document.body.innerHTML = "";
  vi.restoreAllMocks();
});

describe("confirmation forms", () => {
  test("prevents submission when confirmation is rejected", () => {
    document.body.innerHTML = '<form data-confirm="Delete permanently?"><button>Delete</button></form>';
    vi.spyOn(window, "confirm").mockReturnValue(false);
    const event = new Event("submit", { bubbles: true, cancelable: true });

    document.querySelector("form").dispatchEvent(event);

    expect(window.confirm).toHaveBeenCalledWith("Delete permanently?");
    expect(event.defaultPrevented).toBe(true);
  });

  test("allows confirmed and ordinary forms", () => {
    document.body.innerHTML = '<form data-confirm="Continue?"></form><form id="ordinary"></form>';
    vi.spyOn(window, "confirm").mockReturnValue(true);
    const confirmed = new Event("submit", { bubbles: true, cancelable: true });
    const ordinary = new Event("submit", { bubbles: true, cancelable: true });

    document.querySelector("form[data-confirm]").dispatchEvent(confirmed);
    document.querySelector("#ordinary").dispatchEvent(ordinary);

    expect(confirmed.defaultPrevented).toBe(false);
    expect(ordinary.defaultPrevented).toBe(false);
  });
});

describe("in-page navigation", () => {
  test("scrolls to and focuses the requested section", () => {
    document.body.innerHTML = '<a data-scroll-target="posts">Explore</a><section id="posts" tabindex="-1"></section>';
    const target = document.querySelector("#posts");
    target.scrollIntoView = vi.fn();
    target.focus = vi.fn();
    vi.spyOn(window, "requestAnimationFrame").mockImplementation((callback) => callback());
    vi.spyOn(window.history, "replaceState");

    document.querySelector("a").click();

    expect(target.scrollIntoView).toHaveBeenCalledWith({ behavior: "smooth", block: "start" });
    expect(target.focus).toHaveBeenCalledWith({ preventScroll: true });
    expect(target.classList.contains("is-scroll-highlighted")).toBe(true);
    expect(window.history.replaceState).toHaveBeenCalledWith(null, "", "#posts");
  });

  test("ignores unrelated clicks and missing targets", () => {
    document.body.innerHTML = '<button id="ordinary">Ordinary</button><a data-scroll-target="missing">Missing</a>';
    const ordinary = new MouseEvent("click", { bubbles: true, cancelable: true });
    const missing = new MouseEvent("click", { bubbles: true, cancelable: true });

    document.querySelector("#ordinary").dispatchEvent(ordinary);
    document.querySelector("a").dispatchEvent(missing);

    expect(ordinary.defaultPrevented).toBe(false);
    expect(missing.defaultPrevented).toBe(false);
  });
});
