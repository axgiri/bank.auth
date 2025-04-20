package github.axgiri.handler;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import github.axgiri.bankauthentication.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    record ApiError(Instant timestamp, String code, String message, Object details) {}

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> onNotFound(UserNotFoundException ex) {
        ApiError err = new ApiError(
            Instant.now(),
            "USER_NOT_FOUND",
            ex.getMessage(),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> onValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());
        ApiError err = new ApiError(
            Instant.now(),
            "VALIDATION_ERROR",
            "Invalid request data",
            errors
        );
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> onGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiError err = new ApiError(
            Instant.now(),
            "SERVER_ERROR",
            "Unexpected error",
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}

