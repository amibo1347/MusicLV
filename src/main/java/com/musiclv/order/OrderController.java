package com.musiclv.order;

import com.musiclv.cart.CartService;
import com.musiclv.member.Member;
import com.musiclv.member.MemberPrincipal;
import com.musiclv.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final MemberService memberService;

    /** 주문서 작성 화면 — 장바구니 전체를 주문한다. */
    @GetMapping("/new")
    public String orderForm(@AuthenticationPrincipal MemberPrincipal principal,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        var items = cartService.getItems(principal.getId());
        if (items.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "장바구니가 비어 있습니다.");
            return "redirect:/cart";
        }

        Member member = memberService.getById(principal.getId());
        OrderForm form = new OrderForm();
        form.setReceiverName(member.getName());
        form.setReceiverPhone(member.getPhone());
        form.setAddress(member.getAddress());

        model.addAttribute("orderForm", form);
        model.addAttribute("items", items);
        model.addAttribute("totalAmount", cartService.getTotalAmount(principal.getId()));
        return "order/form";
    }

    @PostMapping
    public String order(@AuthenticationPrincipal MemberPrincipal principal,
                        @Valid @ModelAttribute OrderForm orderForm,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("items", cartService.getItems(principal.getId()));
            model.addAttribute("totalAmount", cartService.getTotalAmount(principal.getId()));
            return "order/form";
        }

        try {
            Long orderId = orderService.orderFromCart(principal.getId(), orderForm);
            redirectAttributes.addFlashAttribute("message", "주문이 완료되었습니다.");
            return "redirect:/orders/" + orderId;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping
    public String myOrders(@AuthenticationPrincipal MemberPrincipal principal, Model model) {
        model.addAttribute("orders", orderService.getMyOrders(principal.getId()));
        return "order/list";
    }

    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal MemberPrincipal principal,
                         @PathVariable Long id,
                         Model model) {
        model.addAttribute("order", orderService.getOrderFor(id, principal.getId(), principal.isAdmin()));
        return "order/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@AuthenticationPrincipal MemberPrincipal principal,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            orderService.cancel(id, principal.getId(), principal.isAdmin());
            redirectAttributes.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
