const { test, expect } = require("@playwright/test");

test("browse all posts visibly scrolls to the posts section", async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 720 });
  await page.goto("/");

  await expect(page.getByRole("heading", { name: "A practical blog for thoughtful software builders." })).toBeVisible();
  await page.getByRole("link", { name: "Browse all posts" }).click();

  await expect(page).toHaveURL(/#posts$/);
  await expect(page.locator("#posts")).toBeFocused();
  await expect(page.getByRole("heading", { name: "Browse posts" })).toBeVisible();
  await expect(page.locator("#posts")).toHaveClass(/is-scroll-highlighted/);
  await expect.poll(() => page.evaluate(() => window.scrollY)).toBeGreaterThan(100);
  await expect.poll(() => page.locator("#posts").evaluate((element) => element.getBoundingClientRect().top))
    .toBeGreaterThanOrEqual(70);
});

test("footer stays at the bottom of a tall viewport", async ({ page }) => {
  await page.setViewportSize({ width: 1280, height: 1400 });
  await page.goto("/");

  const layout = await page.evaluate(() => {
    const footer = document.querySelector("footer");
    const footerBounds = footer.getBoundingClientRect();

    return {
      bodyDisplay: getComputedStyle(document.body).display,
      bodyDirection: getComputedStyle(document.body).flexDirection,
      bodyMinHeight: getComputedStyle(document.body).minHeight,
      footerBottom: Math.round(footerBounds.bottom),
      viewportBottom: window.innerHeight
    };
  });

  expect(layout.bodyDisplay).toBe("flex");
  expect(layout.bodyDirection).toBe("column");
  expect(layout.bodyMinHeight).toBe("1400px");
  expect(layout.footerBottom).toBe(layout.viewportBottom);
});

test("the full topic row opens its topic page", async ({ page }) => {
  await page.goto("/");
  await page.evaluate(() => {
    const topicList = document.createElement("div");
    topicList.className = "topic-list";
    topicList.style.width = "320px";
    topicList.innerHTML = '<div class="topic-row"><a href="/tags/404" class="topic-link">Java</a></div>';
    document.body.append(topicList);
  });

  const row = page.locator(".topic-row");
  const link = page.getByRole("link", { name: "Java" });
  const rowBox = await row.boundingBox();
  const linkBox = await link.boundingBox();

  expect(linkBox).toEqual(rowBox);
  await row.click({ position: { x: 300, y: 10 } });
  await expect(page).toHaveURL(/\/tags\/404$/);
});

test("registration validates the password and creates an account", async ({ page }) => {
  await page.goto("/register");

  const password = page.getByLabel("Password", { exact: true });
  await page.getByLabel("Username").fill("playwright-user");
  await password.fill("weak");
  await page.getByLabel("Confirm password").fill("weak");
  await page.getByRole("button", { name: "Register" }).click();

  await expect(password).toHaveJSProperty("validity.valid", false);
  await expect(page).toHaveURL(/\/register$/);

  await password.fill("Stable!Pass9");
  await page.getByLabel("Confirm password").fill("Stable!Pass9");
  await page.getByRole("button", { name: "Register" }).click();

  await expect(page).toHaveURL(/\/login\?registered$/);
  await expect(page.getByText("Your account is ready. You can now log in.")).toBeVisible();
});
