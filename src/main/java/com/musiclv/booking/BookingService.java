package com.musiclv.booking;

import com.musiclv.member.MemberService;
import com.musiclv.performance.Performance;
import com.musiclv.performance.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MemberService memberService;
    private final PerformanceService performanceService;

    @Transactional
    public Long book(Long memberId, Long performanceId, BookingForm form) {
        Performance performance = performanceService.getById(performanceId);

        if (performance.isClosed()) {
            throw new IllegalStateException("이미 종료된 공연입니다.");
        }
        if (form.getViewDate().isBefore(performance.getStartDate())
                || form.getViewDate().isAfter(performance.getEndDate())) {
            throw new IllegalArgumentException("관람일은 공연 기간 안에서 선택해주세요.");
        }

        Booking booking = Booking.create(
                memberService.getById(memberId),
                performance,
                form.getViewDate(),
                form.getQuantity(),
                form.getBookerName(),
                form.getBookerPhone()
        );
        return bookingRepository.save(booking).getId();
    }

    public List<Booking> getMyBookings(Long memberId) {
        return bookingRepository.findByMemberOrderByIdDesc(memberService.getById(memberId));
    }

    /** 본인 예매이거나 관리자일 때만 조회할 수 있다. */
    public Booking getBookingFor(Long bookingId, Long memberId, boolean admin) {
        Booking booking = bookingRepository.findWithDetailById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. id=" + bookingId));
        if (!admin && !booking.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 예매가 아닙니다.");
        }
        return booking;
    }

    @Transactional
    public void cancel(Long bookingId, Long memberId, boolean admin) {
        getBookingFor(bookingId, memberId, admin).cancel();
    }

    // ----- 관리자 -----

    public Page<AdminBookingRow> getBookingsForAdmin(BookingStatus status, Pageable pageable) {
        return bookingRepository.findForAdmin(status, pageable).map(AdminBookingRow::from);
    }

    @Transactional
    public void changeStatus(Long bookingId, BookingStatus status) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. id=" + bookingId))
                .changeStatus(status);
    }
}
