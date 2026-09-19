package com.example.cineflow.domain.screen;

import com.example.cineflow.domain.common.BaseTimeEntity;
import com.example.cineflow.domain.theater.Theater;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "screens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_screen_theater_name", columnNames = {"theater_id", "name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Screen extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id", nullable = false, foreignKey = @ForeignKey(name = "fk_screen_theater"))
    private Theater theater;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int totalSeats;

    @Builder
    private Screen(Theater theater, String name, int totalSeats) {
        this.theater = theater;
        this.name = name;
        this.totalSeats = totalSeats;
    }
}
