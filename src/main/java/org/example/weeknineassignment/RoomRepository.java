package org.example.weeknineassignment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByCapacityGreaterThanEqual(int minCapacity, Sort sort);

    List<Room> findByNameContainingIgnoreCase(String keyword, Sort sort);

    List<Room> findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(
            int minCapacity, String keyword, Sort sort);
}
