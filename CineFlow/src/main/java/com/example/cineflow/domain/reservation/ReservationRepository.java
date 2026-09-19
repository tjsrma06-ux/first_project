package com.example.cineflow.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Reservation> findByScreeningIdAndSeatId(Long screeningId, Long seatId);

    boolean existsByScreeningIdAndSeatId(Long screeningId, Long seatId);
}
