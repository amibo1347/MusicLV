package com.musiclv.admin;

import com.musiclv.order.OrderService;
import com.musiclv.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final int PAGE_SIZE = 20;

    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("orders", orderService.getOrdersForAdmin(status,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE)));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-list";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        orderService.changeStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "주문 상태를 " + status.getLabel() + " 로 변경했습니다.");
        return "redirect:/admin/orders";
    }
}
