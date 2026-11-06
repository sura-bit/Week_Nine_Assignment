package org.example.weeknineassignment;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        List<FieldMessage> fieldErrors
) {

    public record FieldMessage(
            String field,
            String message
    ) {}

    public static ApiError of(
            HttpStatus status,
            String message,
            String path,
            List<FieldMessage> fieldErrors
    ) {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fieldErrors
        );
    }
}
