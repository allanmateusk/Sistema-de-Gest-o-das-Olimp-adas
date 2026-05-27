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

        // Esse trecho está bom, porque aproveita um requestId enviado pelo cliente ou cria um novo quando não existe.
        // Isso ajuda bastante para acompanhar uma requisição nos logs.

        if (request instanceof MutableHttpRequest<?> mutable) {
            mutable.setAttribute(RequestIdContext.REQUEST_ATTRIBUTE, id);
        }

        // Seria bom criar uma constante para a string "requestId".
        // Como ela também pode ser usada em outros pontos do sistema, isso evita erro de digitação.

        MDC.put("requestId", id);

        @SuppressWarnings("unchecked")
        Publisher<MutableHttpResponse<?>> next = (Publisher<MutableHttpResponse<?>>) (Publisher<?>) chain.proceed(request);

        // Esses casts deixam o código um pouco difícil de ler.
        // Se possível, vale tentar simplificar essa parte para reduzir a necessidade de @SuppressWarnings.

        @SuppressWarnings("unchecked")
        Publisher<MutableHttpResponse<?>> out = (Publisher<MutableHttpResponse<?>>) (Object) Mono.from(next)
                .map(response -> {
                    if (!response.getHeaders().contains(RequestIdContext.HEADER)) {
                        response.getHeaders().add(RequestIdContext.HEADER, id);
                    }

                    // Está correto adicionar o requestId na resposta.
                    // Isso facilita para o front-end ou para quem testar a API encontrar o mesmo id nos logs.

                    return response;
                })
                .doFinally(s -> MDC.remove("requestId"));

        // O uso do doFinally é importante, porque garante que o MDC seja limpo no final da requisição.
        // Isso evita que o requestId de uma chamada apareça por engano no log de outra.

        return out;
    }
}
