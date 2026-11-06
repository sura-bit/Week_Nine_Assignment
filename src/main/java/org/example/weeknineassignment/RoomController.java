package org.example.weeknineassignment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<RoomResponse> findAll(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort) {

        return roomService.search(minCapacity, keyword, sort)
                .stream()
                .map(RoomResponse::from)
                .toList();
    }

    @GetMapping("/count")
    public long count() {
        return roomService.count();
    }

    @GetMapping("/{id}")
    public RoomResponse findOne(@PathVariable Long id) {
        return RoomResponse.from(roomService.get(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @Valid @RequestBody RoomCreateRequest request) {

        Room room = roomService.create(
                request.name(),
                request.capacity()
        );

        return ResponseEntity
                .created(URI.create("/api/rooms/" + room.getId()))
                .body(RoomResponse.from(room));
    }

    @PutMapping("/{id}")
    public RoomResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RoomCreateRequest request) {

        return RoomResponse.from(
                roomService.update(
                        id,
                        request.name(),
                        request.capacity()
                )
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}