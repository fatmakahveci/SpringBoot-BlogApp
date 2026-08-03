const { defineConfig } = require("vitest/config");

module.exports = defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    include: ["tests/frontend/**/*.test.js"],
    coverage: {
      provider: "v8",
      include: ["src/main/resources/static/scripts/**/*.js"],
      reporter: ["text", "html", "json-summary"],
      reportsDirectory: "coverage/frontend",
      thresholds: {
        lines: 90,
        functions: 90,
        branches: 80,
        statements: 90
      }
    }
  }
});
