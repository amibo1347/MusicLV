package com.musiclv.admin;

import com.musiclv.booking.BookingService;
import com.musiclv.booking.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private static final int PAGE_SIZE = 20;

    private final BookingService bookingService;

    @GetMapping
    public String list(@RequestParam(required = false) BookingStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("bookings", bookingService.getBookingsForAdmin(status,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE)));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/booking-list";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam BookingStatus status,
                               RedirectAttributes redirectAttributes) {
        bookingService.changeStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "예매 상태를 " + status.getLabel() + " 로 변경했습니다.");
        return "redirect:/admin/bookings";
    }
}
