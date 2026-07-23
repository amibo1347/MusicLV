package com.musiclv.member;

import com.musiclv.booking.BookingService;
import com.musiclv.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final OrderService orderService;
    private final BookingService bookingService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupForm signupForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if (!signupForm.isPasswordMatched()) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "비밀번호가 일치하지 않습니다.");
        }
        if (signupForm.getEmail() != null && memberService.isEmailTaken(signupForm.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "이미 가입된 이메일입니다.");
        }
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        memberService.signup(signupForm);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/members/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal MemberPrincipal principal, Model model) {
        model.addAttribute("member", memberService.getById(principal.getId()));
        model.addAttribute("orders", orderService.getMyOrders(principal.getId()));
        model.addAttribute("bookings", bookingService.getMyBookings(principal.getId()));
        return "member/mypage";
    }
}
