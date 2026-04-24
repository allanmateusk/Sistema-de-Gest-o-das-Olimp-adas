package com.sgo.presentation.filter;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.micronaut.core.order.Ordered.HIGHEST_PRECEDENCE;

/**
 * Gera ou repete {@link RequestIdContext#HEADER}, atribui ao MDC, à requisição e repete na resposta.
 */
@Singleton
@Filter("/**")
public class RequestIdFilter implements HttpServerFilter, Ordered {

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String id = request.getHeaders()
                .getFirst(RequestIdContext.HEADER)
                .filter(s -> !s.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());
        if (request instanceof MutableHttpRequest<?> mutable) {
            mutable.setAttribute(RequestIdContext.REQUEST_ATTRIBUTE, id);
        }
        MDC.put("requestId", id);
        @SuppressWarnings("unchecked")
        Publisher<MutableHttpResponse<?>> next = (Publisher<MutableHttpResponse<?>>) (Publisher<?>) chain.proceed(request);
        @SuppressWarnings("unchecked")
        Publisher<MutableHttpResponse<?>> out = (Publisher<MutableHttpResponse<?>>) (Object) Mono.from(next)
                .map(response -> {
                    if (!response.getHeaders().contains(RequestIdContext.HEADER)) {
                        response.getHeaders().add(RequestIdContext.HEADER, id);
                    }
                    return response;
                })
                .doFinally(s -> MDC.remove("requestId"));
        return out;
    }
}
