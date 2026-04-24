package com.sgo.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingWindowRateLimiterTest {

    @Test
    void permiteAteNRegistrosNaJanela() {
        var limiter = new SlidingWindowRateLimiter(2, 60_000L);
        assertTrue(limiter.allow("a"));
        assertTrue(limiter.allow("a"));
        assertFalse(limiter.allow("a"));
    }

    @Test
    void chavesIndependentes() {
        var limiter = new SlidingWindowRateLimiter(1, 60_000L);
        assertTrue(limiter.allow("a"));
        assertTrue(limiter.allow("b"));
    }
}
