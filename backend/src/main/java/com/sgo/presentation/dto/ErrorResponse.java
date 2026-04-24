package com.sgo.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String path,
        String requestId
) {
    public static ErrorResponse of(String title, int status, String detail, String path) {
        return of(title, status, detail, path, null);
    }

    public static ErrorResponse of(String title, int status, String detail, String path, String requestId) {
        return new ErrorResponse("about:blank", title, status, detail, path, requestId);
    }
}
