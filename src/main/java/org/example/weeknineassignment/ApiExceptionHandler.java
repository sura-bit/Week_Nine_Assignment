package org.example.weeknineassignment;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidFields(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        List<ApiError.FieldMessage> fields =
                e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(f -> new ApiError.FieldMessage(
                                f.getField(),
                                f.getDefaultMessage()
                        ))
                        .toList();

        return respond(
                HttpStatus.BAD_REQUEST,
                "request has invalid fields",
                request,
                fields
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(
            NotFoundException e,
            HttpServletRequest request) {

        return respond(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> conflict(
            ConflictException e,
            HttpServletRequest request) {

        return respond(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(
            IllegalArgumentException e,
            HttpServletRequest request) {

        return respond(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> unreadableBody(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {

        return respond(
                HttpStatus.BAD_REQUEST,
                "request body is missing or is not valid JSON",
                request,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> wrongParameterType(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {

        String message =
                "'" + e.getName() + "' has the wrong type";

        return respond(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(
            Exception e,
            HttpServletRequest request) {

        // Preserve Spring's own HTTP status.
        if (e instanceof ErrorResponse springError) {

            HttpStatus status = HttpStatus.valueOf(
                    springError.getStatusCode().value()
            );

            String message = springError.getBody().getDetail();

            if (message == null || message.isBlank()) {
                message = status.getReasonPhrase();
            }

            return respond(
                    status,
                    message,
                    request,
                    List.of()
            );
        }

        // Unexpected server error.
        log.error(
                "Unexpected error on {} {}",
                request.getMethod(),
                request.getRequestURI(),
                e
        );

        return respond(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "unexpected server error",
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiError> respond(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<ApiError.FieldMessage> fieldErrors) {

        ApiError body = ApiError.of(
                status,
                message,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
