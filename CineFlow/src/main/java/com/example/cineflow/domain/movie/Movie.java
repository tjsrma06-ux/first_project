package com.example.cineflow.domain.movie;

import com.example.cineflow.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String synopsis;

    @Column(nullable = false)
    private int runningTimeMinutes;

    @Column(length = 20)
    private String ageRating;

    private LocalDate releaseDate;

    @Column(length = 500)
    private String posterUrl;

    @Builder
    private Movie(String title, String synopsis, int runningTimeMinutes, String ageRating,
                  LocalDate releaseDate, String posterUrl) {
        this.title = title;
        this.synopsis = synopsis;
        this.runningTimeMinutes = runningTimeMinutes;
        this.ageRating = ageRating;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
    }
}
