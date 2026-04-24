package com.sgo.security;

import io.micronaut.http.HttpRequest;

/**
 * Determina a chave por cliente (IP ou primeiro endereço de X-Forwarded-For) para o rate limit de login.
 */
public final class LoginClientKeyResolver {

    public static String resolve(HttpRequest<?> request) {
        String xff = request.getHeaders()
                .getFirst("X-Forwarded-For", String.class, null);
        if (xff != null && !xff.isBlank()) {
            String part = xff.split(",")[0].trim();
            if (!part.isEmpty()) {
                return part;
            }
        }
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getHostString();
        }
        return "unknown";
    }

    private LoginClientKeyResolver() {
    }
}
