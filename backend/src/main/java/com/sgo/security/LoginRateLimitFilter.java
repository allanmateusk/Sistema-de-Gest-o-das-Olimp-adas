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

        // A validação do método POST está bem colocada.
        // Como o filtro está na rota /auth/login, isso evita aplicar limite em métodos que não fazem login de verdade.

        String key = LoginClientKeyResolver.resolve(request);

        // Esse ponto é importante, porque o limite depende da chave gerada pelo cliente.
        // Seria bom garantir que o LoginClientKeyResolver trate bem casos sem IP ou com X-Forwarded-For mal preenchido.

        if (limitService.allow(key)) {
            return chain.proceed(request);
        }

        String requestId = request.getAttribute(RequestIdContext.REQUEST_ATTRIBUTE, String.class)
                .orElseGet(() -> MDC.get("requestId"));

        // A busca do requestId está boa, porque tenta pegar primeiro da requisição e depois do MDC.
        // Isso ajuda a manter o mesmo identificador nos logs e na resposta da API.

        int retry = Math.max(1, (int) (limitService.getWindowMs() / 1000));

        // O cálculo do Retry-After está simples, mas talvez pudesse considerar o tempo real restante da janela.
        // Do jeito atual, o usuário sempre recebe o tempo total da janela, mesmo que já esteja perto de liberar uma nova tentativa.

        ErrorResponse err = ErrorResponse.of(
                "Too Many Requests",
                HttpStatus.TOO_MANY_REQUESTS.getCode(),
                "Muitas tentativas de login. Aguarde antes de tentar novamente.",
                request.getPath(),
                requestId
        );

        // A resposta 429 está adequada para limite de tentativas.
        // Também é positivo retornar uma mensagem genérica, sem dar detalhes demais sobre a regra de bloqueio.

        return Publishers.just(
                io.micronaut.http.HttpResponse.<ErrorResponse>status(HttpStatus.TOO_MANY_REQUESTS)
                        .header("Retry-After", String.valueOf(retry))
                        .body(err)
        );

        // Como essa classe trata segurança, seria interessante ter testes cobrindo os dois cenários:
        // quando a tentativa é permitida e quando o limite é atingido.
    }
}
