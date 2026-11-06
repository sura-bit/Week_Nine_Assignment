package org.example.weeknineassignment;

import java.time.LocalDateTime;

public record ReservationCreateRequest(String reservedBy, LocalDateTime startTime,
                                       LocalDateTime endTime) {
}
