import { describe, expect, it } from "vitest";
import { resolveApiBaseUrl } from "./config";

describe("resolveApiBaseUrl", () => {
  it("usa /api quando não há VITE", () => {
    expect(resolveApiBaseUrl(undefined)).toBe("/api");
  });

  it("usa URL explícita", () => {
    expect(resolveApiBaseUrl("http://localhost:8080")).toBe("http://localhost:8080");
  });
});
