package com.sgo.presentation.error;

import com.sgo.domain.exception.BusinessException;
import com.sgo.domain.exception.NotFoundException;
import com.sgo.domain.exception.UnauthorizedException;
import com.sgo.presentation.dto.ErrorResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Error;
import jakarta.inject.Singleton;

@Singleton
public class ApiExceptionHandler {

    @Error(global = true, exception = BusinessException.class)
    public HttpResponse<ErrorResponse> onBusiness(HttpRequest<?> request, BusinessException ex) {
        ErrorResponse body = ErrorResponse.of("Conflict", HttpStatus.CONFLICT.getCode(), ex.getMessage(), request.getPath());
        return HttpResponse.status(HttpStatus.CONFLICT).body(body);
    }

    @Error(global = true, exception = NotFoundException.class)
    public HttpResponse<ErrorResponse> onNotFound(HttpRequest<?> request, NotFoundException ex) {
        ErrorResponse body = ErrorResponse.of("Not Found", HttpStatus.NOT_FOUND.getCode(), ex.getMessage(), request.getPath());
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(body);
    }

    @Error(global = true, exception = UnauthorizedException.class)
    public HttpResponse<ErrorResponse> onUnauthorized(HttpRequest<?> request, UnauthorizedException ex) {
        ErrorResponse body = ErrorResponse.of("Unauthorized", HttpStatus.UNAUTHORIZED.getCode(), ex.getMessage(), request.getPath());
        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
