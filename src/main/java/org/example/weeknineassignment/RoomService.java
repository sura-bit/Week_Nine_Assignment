package org.example.weeknineassignment;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> search(Integer minCapacity, String keyword, String sort) {
        Sort order = toSort(sort);
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if (minCapacity != null && hasKeyword) {
            return roomRepository
                    .findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword, order);
        }
        if (minCapacity != null) {
            return roomRepository.findByCapacityGreaterThanEqual(minCapacity, order);
        }
        if (hasKeyword) {
            return roomRepository.findByNameContainingIgnoreCase(keyword, order);
        }
        return roomRepository.findAll(order);
    }

    public long count() {
        return roomRepository.count();
    }

    public Room get(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("room " + id + " not found"));
    }

    private Sort toSort(String sort) {
        if ("capacity".equals(sort)) return Sort.by("capacity");
        if ("name".equals(sort)) return Sort.by("name");
        return Sort.by("id");
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Room create(String name, int capacity) {

        checkCapacity(capacity);

        return roomRepository.save(
                new Room(null, name, capacity)
        );
    }

    @Transactional
    public Room update(Long id, String name, int capacity) {
        Room room = get(id);
        room.update(name, capacity);
        return room;
    }

    public boolean delete(Long id) {
        if (!roomRepository.existsById(id)) {
            return false;
        }

        if (reservationRepository.existsByRoomId(id)) {
            throw new ConflictException("room " + id + " has reservations");
        }

        roomRepository.deleteById(id);
        return true;
    }

    public void checkCapacity(int capacity) {
        if (capacity < 1 || capacity > 20) {
            throw new IllegalArgumentException(
                    "Capacity must be between 1 and 20"
            );
        }
    }

}
