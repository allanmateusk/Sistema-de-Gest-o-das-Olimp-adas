package com.sgo.security;

import com.sgo.presentation.dto.ErrorResponse;
import com.sgo.presentation.filter.RequestIdContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;

import static io.micronaut.core.order.Ordered.HIGHEST_PRECEDENCE;

/**
 * Limita tentativas de POST /auth/login por chave (IP / X-Forwarded-For) em janela móvel.
 * Desative com sgo.security.login-rate.enabled=false (ex.: testes).
 */
@Singleton
@Requires(property = "sgo.security.login-rate.enabled", notEquals = "false")
@Filter("/auth/login")
public class LoginRateLimitFilter implements HttpServerFilter, Ordered {

    private static final int ORDER_AFTER_REQUEST_ID = HIGHEST_PRECEDENCE + 10;

    private final LoginRateLimitService limitService;

    public LoginRateLimitFilter(LoginRateLimitService limitService) {
        this.limitService = limitService;
    }

    @Override
    public int getOrder() {
        return ORDER_AFTER_REQUEST_ID;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        if (!request.getMethod().equals(HttpMethod.POST)) {
            return chain.proceed(request);
        }
        String key = LoginClientKeyResolver.resolve(request);
        if (limitService.allow(key)) {
            return chain.proceed(request);
        }
        String requestId = request.getAttribute(RequestIdContext.REQUEST_ATTRIBUTE, String.class)
                .orElseGet(() -> MDC.get("requestId"));
        int retry = Math.max(1, (int) (limitService.getWindowMs() / 1000));
        ErrorResponse err = ErrorResponse.of(
                "Too Many Requests",
                HttpStatus.TOO_MANY_REQUESTS.getCode(),
                "Muitas tentativas de login. Aguarde antes de tentar novamente.",
                request.getPath(),
                requestId
        );
        return Publishers.just(
                io.micronaut.http.HttpResponse.<ErrorResponse>status(HttpStatus.TOO_MANY_REQUESTS)
                        .header("Retry-After", String.valueOf(retry))
                        .body(err)
        );
    }
}
