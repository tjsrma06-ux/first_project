package com.example.cineflow.domain.screening;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByMovieIdAndStartsAtAfterOrderByStartsAtAsc(Long movieId, LocalDateTime startsAt);

    List<Screening> findByScreenIdAndStartsAtBetween(Long screenId, LocalDateTime from, LocalDateTime to);
}
