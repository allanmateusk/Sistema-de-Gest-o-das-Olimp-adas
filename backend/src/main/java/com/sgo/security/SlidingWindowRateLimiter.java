package com.sgo.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contagem por chave com janela móvel (tempo a partir do primeiro registro no bucket).
 */
public class SlidingWindowRateLimiter {

    private final int maxAttempts;
    private final long windowMs;
    private final ConcurrentHashMap<String, Deque<Long>> byKey = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxAttempts, long windowMs) {
        this.maxAttempts = maxAttempts;
        this.windowMs = windowMs;
    }

    public long getWindowMs() {
        return windowMs;
    }

    /**
     * @return true se a requisição cabe no limite; false se excedeu
     */
    public boolean allow(String key) {
        if (key == null || key.isBlank()) {
            return true;
        }
        long now = System.currentTimeMillis();
        Deque<Long> window = byKey.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty() && now - window.peekFirst() > windowMs) {
                window.pollFirst();
            }
            if (window.size() >= maxAttempts) {
                return false;
            }
            window.addLast(now);
            return true;
        }
    }
}
