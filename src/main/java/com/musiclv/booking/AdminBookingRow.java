package com.musiclv.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 관리자 예매 목록의 한 줄.
 * open-in-view=false 라서 뷰에서 지연 로딩이 불가능하므로
 * 트랜잭션 안에서 필요한 값만 옮겨 담는다.
 */
public record AdminBookingRow(
        Long id,
        LocalDateTime bookedAt,
        String memberName,
        String memberEmail,
        String performanceTitle,
        LocalDate viewDate,
        int quantity,
        int totalAmount,
        BookingStatus status
) {
    public static AdminBookingRow from(Booking b) {
        return new AdminBookingRow(
                b.getId(),
                b.getBookedAt(),
                b.getMember().getName(),
                b.getMember().getEmail(),
                b.getPerformance().getTitle(),
                b.getViewDate(),
                b.getQuantity(),
                b.getTotalAmount(),
                b.getStatus()
        );
    }
}
