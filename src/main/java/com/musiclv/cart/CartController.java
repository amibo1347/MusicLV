package com.musiclv.cart;

import com.musiclv.member.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String view(@AuthenticationPrincipal MemberPrincipal principal, Model model) {
        model.addAttribute("items", cartService.getItems(principal.getId()));
        model.addAttribute("totalAmount", cartService.getTotalAmount(principal.getId()));
        return "cart/view";
    }

    @PostMapping("/add")
    public String add(@AuthenticationPrincipal MemberPrincipal principal,
                      @RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      @RequestParam(defaultValue = "false") boolean buyNow,
                      RedirectAttributes redirectAttributes) {
        try {
            cartService.add(principal.getId(), productId, quantity);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }

        // "바로 구매하기" 는 장바구니를 거쳐 주문서로 바로 보낸다.
        if (buyNow) {
            return "redirect:/orders/new";
        }
        redirectAttributes.addFlashAttribute("message", "장바구니에 담았습니다.");
        return "redirect:/cart";
    }

    @PostMapping("/{cartItemId}/quantity")
    public String changeQuantity(@AuthenticationPrincipal MemberPrincipal principal,
                                 @PathVariable Long cartItemId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.changeQuantity(principal.getId(), cartItemId, quantity);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/{cartItemId}/remove")
    public String remove(@AuthenticationPrincipal MemberPrincipal principal,
                         @PathVariable Long cartItemId) {
        cartService.remove(principal.getId(), cartItemId);
        return "redirect:/cart";
    }
}
