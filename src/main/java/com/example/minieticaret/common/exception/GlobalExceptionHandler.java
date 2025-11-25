package com.example.minieticaret.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                         HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.VALIDATION_ERROR,
                "Gecersiz alanlar var", extractFieldErrors(ex.getBindingResult()), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        Map<String, String> validation = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.CONSTRAINT_VIOLATION,
                "Gecersiz veri", validation, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                 HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ApiErrorCode.DATA_INTEGRITY,
                "Veri butunlugu ihlali", null, request);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthenticationException ex,
                                                       HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ApiErrorCode.FORBIDDEN,
                "Kullanici veya sifre hatali", null, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                   HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ApiErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), null, request);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorResponse> handleErrorResponse(ErrorResponseException ex,
                                                                 HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus effectiveStatus = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorCode code = switch (effectiveStatus) {
            case NOT_FOUND -> ApiErrorCode.NOT_FOUND;
            case FORBIDDEN -> ApiErrorCode.FORBIDDEN;
            default -> ApiErrorCode.HTTP_ERROR;
        };
        return buildError(effectiveStatus, code,
                ex.getMessage() != null ? ex.getMessage() : "Hata", null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex,
                                                           HttpServletRequest request) {
        log.error("Unhandled exception at {} {}", request.getMethod(), request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorCode.INTERNAL_ERROR,
                "Bilinmeyen hata", null, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex,
                                                            HttpServletRequest request) {
        return buildError(ex.getStatus(), ex.getCode(), ex.getMessage(), null, request);
    }

    private Map<String, String> extractFieldErrors(BindingResult bindingResult) {
        Map<String, String> validation = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            validation.put(error.getField(), error.getDefaultMessage());
        }
        return validation;
    }

    private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status,
                                                        ApiErrorCode code,
                                                        String message,
                                                        Map<String, String> validation,
                                                        HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                request.getRequestURI(),
                code,
                message,
                validation == null || validation.isEmpty() ? null : validation
        );
        return ResponseEntity.status(status).body(body);
    }
}
