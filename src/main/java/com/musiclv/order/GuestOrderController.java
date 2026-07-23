package com.musiclv.order;

import com.musiclv.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 비회원 주문. 로그인 없이 상품 하나를 바로 주문하고,
 * 발급된 주문번호와 연락처로 나중에 다시 조회한다.
 */
@Controller
@RequestMapping("/orders/guest")
@RequiredArgsConstructor
public class GuestOrderController {

    private final OrderService orderService;
    private final ProductService productService;

    /** 비회원 주문서 */
    @GetMapping("/new")
    public String form(@RequestParam Long productId,
                       @RequestParam(defaultValue = "1") int quantity,
                       Model model) {
        GuestOrderForm form = new GuestOrderForm();
        form.setProductId(productId);
        form.setQuantity(Math.max(quantity, 1));

        model.addAttribute("guestOrderForm", form);
        model.addAttribute("product", productService.getById(productId));
        return "order/guest-form";
    }

    @PostMapping("/new")
    public String order(@Valid @ModelAttribute GuestOrderForm guestOrderForm,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", productService.getById(guestOrderForm.getProductId()));
            return "order/guest-form";
        }

        try {
            Order order = orderService.orderAsGuest(guestOrderForm);
            // 주문번호는 다음 화면에서 크게 보여준다.
            redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
            return "redirect:/orders/guest/complete";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("product", productService.getById(guestOrderForm.getProductId()));
            model.addAttribute("error", e.getMessage());
            return "order/guest-form";
        }
    }

    /** 주문 완료 — 주문번호 안내 */
    @GetMapping("/complete")
    public String complete(@ModelAttribute("orderNumber") String orderNumber,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (orderNumber == null || orderNumber.isBlank()) {
            // 새로고침 등으로 flash 가 사라진 경우
            redirectAttributes.addFlashAttribute("error", "주문 정보를 찾을 수 없습니다. 주문번호로 조회해주세요.");
            return "redirect:/orders/guest";
        }
        model.addAttribute("lookupForm", new GuestLookupForm());
        return "order/guest-complete";
    }

    /** 주문 조회 폼 */
    @GetMapping
    public String lookupForm(Model model) {
        model.addAttribute("lookupForm", new GuestLookupForm());
        return "order/guest-lookup";
    }

    @PostMapping
    public String lookup(@Valid @ModelAttribute("lookupForm") GuestLookupForm lookupForm,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "order/guest-lookup";
        }

        try {
            Order order = orderService.findGuestOrder(lookupForm.getOrderNumber(), lookupForm.getPhone());
            model.addAttribute("order", order);
            model.addAttribute("guestView", true);
            return "order/detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "order/guest-lookup";
        }
    }
}
