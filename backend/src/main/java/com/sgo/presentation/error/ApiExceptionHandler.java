package com.sgo.presentation.error;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.domain.exception.UnauthorizedException;
import com.sgo.presentation.dto.ErrorResponse;
import com.sgo.presentation.filter.RequestIdContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import io.micronaut.serde.exceptions.SerdeException;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.stream.Collectors;

@Singleton
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final String MSG_INTERNAL = "Ocorreu um erro interno. Tente novamente em instantes.";

    @Error(global = true, exception = BusinessException.class)
    public HttpResponse<ErrorResponse> onBusiness(HttpRequest<?> request, BusinessException ex) {
        ErrorResponse body = ErrorResponse.of("Conflict", HttpStatus.CONFLICT.getCode(), ex.getMessage(), request.getPath(), requestIdOrNull(request));
        return HttpResponse.status(HttpStatus.CONFLICT).body(body);
    }

    @Error(global = true, exception = NotFoundException.class)
    public HttpResponse<ErrorResponse> onNotFound(HttpRequest<?> request, NotFoundException ex) {
        ErrorResponse body = ErrorResponse.of("Not Found", HttpStatus.NOT_FOUND.getCode(), ex.getMessage(), request.getPath(), requestIdOrNull(request));
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(body);
    }

    @Error(global = true, exception = UnauthorizedException.class)
    public HttpResponse<ErrorResponse> onUnauthorized(HttpRequest<?> request, UnauthorizedException ex) {
        ErrorResponse body = ErrorResponse.of("Unauthorized", HttpStatus.UNAUTHORIZED.getCode(), ex.getMessage(), request.getPath(), requestIdOrNull(request));
        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @Error(global = true, exception = ConstraintViolationException.class)
    public HttpResponse<ErrorResponse> onConstraint(HttpRequest<?> request, ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.joining("; "));
        if (detail.isEmpty()) {
            detail = "Requisição inválida";
        }
        ErrorResponse body = ErrorResponse.of("Bad Request", HttpStatus.BAD_REQUEST.getCode(), detail, request.getPath(), requestIdOrNull(request));
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Error(global = true, exception = SerdeException.class)
    public HttpResponse<ErrorResponse> onSerde(HttpRequest<?> request, SerdeException ex) {
        String detail = ex.getMessage() != null ? ex.getMessage() : "JSON inválido";
        ErrorResponse body = ErrorResponse.of("Bad Request", HttpStatus.BAD_REQUEST.getCode(), detail, request.getPath(), requestIdOrNull(request));
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Error(global = true, exception = Exception.class)
    public HttpResponse<ErrorResponse> onAny(HttpRequest<?> request, Exception ex) {
        LOG.error("requestId={} Unhandled: {}", requestIdOrNull(request), ex.toString(), ex);
        ErrorResponse body = ErrorResponse.of(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.getCode(),
                MSG_INTERNAL,
                request.getPath(),
                requestIdOrNull(request)
        );
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String formatViolation(ConstraintViolation<?> v) {
        String p = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        if (p == null || p.isEmpty()) {
            return v.getMessage();
        }
        return p + ": " + v.getMessage();
    }

    private String requestIdOrNull(HttpRequest<?> request) {
        return request.getAttribute(RequestIdContext.REQUEST_ATTRIBUTE, String.class)
                .or(() -> java.util.Optional.ofNullable(MDC.get("requestId")))
                .orElse(null);
    }
}
