package org.example.weeknineassignment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(RoomRepository roomRepository,
                              ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    // empty = no such room (404)
    public List<Reservation> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("room " + roomId + " not found");
        }

        return reservationRepository.findByRoomIdOrderByStartTime(roomId);
    }

    @Transactional
    public Reservation create(
            Long roomId,
            String reservedBy,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "startTime must be before endTime");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new NotFoundException("room " + roomId + " not found"));

        if (reservationRepository.countOverlapping(
                roomId, startTime, endTime) > 0) {

            throw new ConflictException(
                    "room " + roomId + " is already booked at that time");
        }

        return reservationRepository.save(
                new Reservation(room, reservedBy, startTime, endTime));
    }

    private void checkRequest(String reservedBy, LocalDateTime startTime, LocalDateTime endTime) {
        if (reservedBy == null || reservedBy.isBlank()) {
            throw new IllegalArgumentException("reservedBy is required");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }

    public List<Reservation> findByReservedBy(String name) {
        return reservationRepository.findByReservedByWithRoom(name);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation get(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "reservation " + id + " not found"));
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException(
                    "reservation " + id + " not found");
        }

        reservationRepository.deleteById(id);
    }
}
