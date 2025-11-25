package com.example.minieticaret.common.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        String path,
        ApiErrorCode code,
        String message,
        Map<String, String> validation
) {
}
