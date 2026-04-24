package com.sgo.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
@Requires(property = "sgo.security.login-rate.enabled", notEquals = "false")
public class LoginRateLimitService {

    private final SlidingWindowRateLimiter limiter;

    public LoginRateLimitService(
            @Value("${sgo.security.login-rate.max-attempts:20}") int maxAttempts,
            @Value("${sgo.security.login-rate.window-seconds:60}") int windowSeconds
    ) {
        this.limiter = new SlidingWindowRateLimiter(maxAttempts, (long) windowSeconds * 1000L);
    }

    public long getWindowMs() {
        return limiter.getWindowMs();
    }

    /**
     * @return true se a tentativa de login é permitida, false se excedeu a janela
     */
    public boolean allow(String key) {
        return limiter.allow(key);
    }
}
