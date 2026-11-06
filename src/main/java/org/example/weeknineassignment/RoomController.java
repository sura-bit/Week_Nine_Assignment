package org.example.weeknineassignment;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET /api/rooms
    @GetMapping
    public List<Room> findAll(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort) {
        return roomService.search(minCapacity, keyword, sort);
    }

    @GetMapping("/count")
    public long count() {
        return roomService.count();
    }

    // GET /api/rooms/{id}
    @Operation(summary = "Get a room by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Room> findOne(@PathVariable Long id) {

        return roomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/rooms
    @Operation(summary = "Create a new room")
    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room room) {

        try {
            Room newRoom = roomService.create(
                    room.getName(),
                    room.getCapacity()
            );

            return ResponseEntity
                    .created(URI.create("/api/rooms/" + newRoom.getId()))
                    .body(newRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/rooms/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Room> update(
            @PathVariable Long id,
            @RequestBody Room room) {

        try {
            return roomService.update(
                            id,
                            room.getName(),
                            room.getCapacity()
                    )
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/rooms/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (roomService.delete(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
