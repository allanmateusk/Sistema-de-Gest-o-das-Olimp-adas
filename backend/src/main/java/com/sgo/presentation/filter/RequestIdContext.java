package com.sgo.presentation.filter;

/**
 * Chave do atributo de requisição (requestId) e cabeçalho HTTP padrão.
 */
public final class RequestIdContext {

    public static final String HEADER = "X-Request-ID";
    public static final String REQUEST_ATTRIBUTE = "sgoRequestId";

    private RequestIdContext() {
    }
}
