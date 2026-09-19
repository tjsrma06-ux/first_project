package com.example.cineflow.domain.reservation;

import com.example.cineflow.domain.common.BaseTimeEntity;
import com.example.cineflow.domain.screening.Screening;
import com.example.cineflow.domain.seat.Seat;
import com.example.cineflow.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "reservations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_reservation_screening_seat", columnNames = {"screening_id", "seat_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String reservationCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screening_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_screening"))
    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_seat"))
    private Seat seat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Builder
    private Reservation(String reservationCode, User user, Screening screening, Seat seat,
                        BigDecimal paidPrice, ReservationStatus status) {
        this.reservationCode = reservationCode;
        this.user = user;
        this.screening = screening;
        this.seat = seat;
        this.paidPrice = paidPrice;
        this.status = status == null ? ReservationStatus.CONFIRMED : status;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}
