package com.musiclv.common;

import com.musiclv.cart.CartService;
import com.musiclv.member.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 헤더의 장바구니 배지처럼 모든 화면에서 필요한 값을 채워준다.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CartService cartService;

    @ModelAttribute("cartCount")
    public long cartCount(@AuthenticationPrincipal MemberPrincipal principal) {
        if (principal == null) {
            return 0L;
        }
        return cartService.getItemCount(principal.getId());
    }
}
