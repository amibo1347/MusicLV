package com.musiclv.performance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance", indexes = {
        @Index(name = "idx_performance_title", columnList = "title"),
        @Index(name = "idx_performance_category", columnList = "category")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerformanceCategory category;

    /** 공연장 */
    @Column(nullable = false, length = 120)
    private String venue;

    /** 출연진. CAST 는 SQL 예약어라 컬럼명을 따로 지정한다. */
    @Column(name = "cast_members", length = 255)
    private String cast;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    /** 관람 시간(분) */
    @Column(nullable = false)
    private int runningTime;

    /** 관람 등급 (예: 8세 이상) */
    @Column(length = 40)
    private String ageRating;

    @Column(nullable = false)
    private int price;

    /** 남은 좌석 */
    @Column(nullable = false)
    private int seats;

    @Column(length = 255)
    private String posterUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Performance(String title, PerformanceCategory category, String venue, String cast,
                        LocalDate startDate, LocalDate endDate, int runningTime, String ageRating,
                        int price, int seats, String posterUrl, String description) {
        this.title = title;
        this.category = category;
        this.venue = venue;
        this.cast = cast;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
        this.price = price;
        this.seats = seats;
        this.posterUrl = posterUrl;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public static Performance of(String title, PerformanceCategory category, String venue, String cast,
                                 LocalDate startDate, LocalDate endDate, int runningTime, String ageRating,
                                 int price, int seats, String posterUrl, String description) {
        return new Performance(title, category, venue, cast, startDate, endDate,
                runningTime, ageRating, price, seats, posterUrl, description);
    }

    public void update(String title, PerformanceCategory category, String venue, String cast,
                       LocalDate startDate, LocalDate endDate, int runningTime, String ageRating,
                       int price, int seats, String posterUrl, String description) {
        this.title = title;
        this.category = category;
        this.venue = venue;
        this.cast = cast;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runningTime = runningTime;
        this.ageRating = ageRating;
        this.price = price;
        this.seats = seats;
        this.description = description;
        if (posterUrl != null && !posterUrl.isBlank()) {
            this.posterUrl = posterUrl;
        }
    }

    public boolean isSoldOut() {
        return seats <= 0;
    }

    /** 종료일이 지났는지 */
    public boolean isClosed() {
        return endDate.isBefore(LocalDate.now());
    }

    public boolean isBookable() {
        return !isSoldOut() && !isClosed();
    }

    public void reduceSeats(int quantity) {
        int remaining = this.seats - quantity;
        if (remaining < 0) {
            throw new IllegalStateException("남은 좌석이 부족합니다. 공연: " + title + ", 잔여: " + seats);
        }
        this.seats = remaining;
    }

    public void restoreSeats(int quantity) {
        this.seats += quantity;
    }
}
