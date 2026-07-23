package com.musiclv.performance;

import com.musiclv.booking.BookingForm;
import com.musiclv.member.Member;
import com.musiclv.member.MemberPrincipal;
import com.musiclv.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class PerformanceController {

    private static final int PAGE_SIZE = 12;

    private final PerformanceService performanceService;
    private final MemberService memberService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) PerformanceCategory category,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "soon") String sort,
                       Model model) {

        Sort sorting = switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.ASC, "startDate");
        };

        Page<Performance> performances = performanceService.search(keyword, category,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, sorting));

        model.addAttribute("performances", performances);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", PerformanceCategory.values());
        return "performance/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal MemberPrincipal principal,
                         Model model) {

        Performance performance = performanceService.getById(id);

        BookingForm form = new BookingForm();
        form.setQuantity(1);
        form.setViewDate(performance.getStartDate());
        if (principal != null) {
            Member member = memberService.getById(principal.getId());
            form.setBookerName(member.getName());
            form.setBookerPhone(member.getPhone());
        }

        model.addAttribute("performance", performance);
        model.addAttribute("bookingForm", form);
        return "performance/detail";
    }
}
