package org.example.weeknineassignment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<List<ReservationResponse>> listForRoom(@PathVariable Long roomId) {
        return reservationService.findByRoom(roomId)
                .map(list -> ResponseEntity.ok(list.stream()
                        .map(ReservationResponse::from)
                        .toList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResponse> create(@PathVariable Long roomId,
                                                      @RequestBody ReservationCreateRequest request) {
        try {
            return reservationService.create(roomId, request.reservedBy(),
                            request.startTime(), request.endTime())
                    .map(r -> ResponseEntity
                            .created(URI.create("/api/reservations/" + r.getId()))
                            .body(ReservationResponse.from(r)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> search(@RequestParam String reservedBy) {
        return reservationService.findByReservedBy(reservedBy).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponse> findOne(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(r -> ResponseEntity.ok(ReservationResponse.from(r)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reservationService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
