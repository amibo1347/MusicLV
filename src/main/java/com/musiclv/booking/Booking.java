package com.musiclv.booking;

import com.musiclv.member.Member;
import com.musiclv.performance.Performance;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    /** 관람일 */
    @Column(nullable = false)
    private LocalDate viewDate;

    @Column(nullable = false)
    private int quantity;

    /** 예매 시점의 가격을 박제한다 */
    @Column(nullable = false)
    private int bookingPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(nullable = false, length = 50)
    private String bookerName;

    @Column(nullable = false, length = 20)
    private String bookerPhone;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    private Booking(Member member, Performance performance, LocalDate viewDate, int quantity,
                    String bookerName, String bookerPhone) {
        this.member = member;
        this.performance = performance;
        this.viewDate = viewDate;
        this.quantity = quantity;
        this.bookingPrice = performance.getPrice();
        this.bookerName = bookerName;
        this.bookerPhone = bookerPhone;
        this.status = BookingStatus.BOOKED;
        this.bookedAt = LocalDateTime.now();
    }

    public static Booking create(Member member, Performance performance, LocalDate viewDate,
                                 int quantity, String bookerName, String bookerPhone) {
        performance.reduceSeats(quantity);
        return new Booking(member, performance, viewDate, quantity, bookerName, bookerPhone);
    }

    public int getTotalAmount() {
        return bookingPrice * quantity;
    }

    public void cancel() {
        if (!status.isCancellable()) {
            throw new IllegalStateException("이미 " + status.getLabel() + " 상태라 취소할 수 없습니다.");
        }
        performance.restoreSeats(quantity);
        this.status = BookingStatus.CANCELLED;
    }

    public void changeStatus(BookingStatus status) {
        this.status = status;
    }
}
