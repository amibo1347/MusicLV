package com.musiclv;

import com.musiclv.performance.PerformanceService;
import com.musiclv.product.Category;
import com.musiclv.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 소개 · 가이드처럼 DB 없이 보여주는 정적 성격의 페이지.
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final ProductService productService;
    private final PerformanceService performanceService;

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("productCount", productService.count());
        model.addAttribute("performanceCount", performanceService.count());
        return "page/about";
    }

    @GetMapping("/guide")
    public String guide(Model model) {
        model.addAttribute("categories", Category.values());
        return "page/guide";
    }
}
