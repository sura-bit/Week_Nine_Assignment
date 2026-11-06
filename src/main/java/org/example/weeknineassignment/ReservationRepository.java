package org.example.weeknineassignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomIdOrderByStartTime(Long roomId);

    boolean existsByRoomId(Long roomId);

    List<Reservation> findByReservedByOrderByStartTime(String reservedBy);
    @Query("""
        select r from Reservation r
        join fetch r.room
        where r.reservedBy = :name
        order by r.startTime
        """)
    List<Reservation> findByReservedByWithRoom(@Param("name") String name);

    @Query("""
        select count(r) from Reservation r
        where r.room.id = :roomId
          and r.startTime < :endTime
          and r.endTime > :startTime
        """)
    long countOverlapping(@Param("roomId") Long roomId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

}
