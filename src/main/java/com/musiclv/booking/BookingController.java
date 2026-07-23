package com.musiclv.booking;

import com.musiclv.member.MemberPrincipal;
import com.musiclv.performance.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PerformanceService performanceService;

    @PostMapping("/tickets/{performanceId}/book")
    public String book(@AuthenticationPrincipal MemberPrincipal principal,
                       @PathVariable Long performanceId,
                       @Valid @ModelAttribute BookingForm bookingForm,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("performance", performanceService.getById(performanceId));
            return "performance/detail";
        }

        try {
            Long bookingId = bookingService.book(principal.getId(), performanceId, bookingForm);
            redirectAttributes.addFlashAttribute("message", "예매가 완료되었습니다.");
            return "redirect:/bookings/" + bookingId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tickets/" + performanceId;
        }
    }

    @GetMapping("/bookings")
    public String myBookings(@AuthenticationPrincipal MemberPrincipal principal, Model model) {
        model.addAttribute("bookings", bookingService.getMyBookings(principal.getId()));
        return "booking/list";
    }

    @GetMapping("/bookings/{id}")
    public String detail(@AuthenticationPrincipal MemberPrincipal principal,
                         @PathVariable Long id,
                         Model model) {
        model.addAttribute("booking",
                bookingService.getBookingFor(id, principal.getId(), principal.isAdmin()));
        return "booking/detail";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@AuthenticationPrincipal MemberPrincipal principal,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancel(id, principal.getId(), principal.isAdmin());
            redirectAttributes.addFlashAttribute("message", "예매가 취소되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/" + id;
    }
}
