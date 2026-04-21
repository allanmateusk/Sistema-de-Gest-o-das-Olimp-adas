package com.sgo.presentation.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String path
) {
    public static ErrorResponse of(String title, int status, String detail, String path) {
        return new ErrorResponse("about:blank", title, status, detail, path);
    }
}
