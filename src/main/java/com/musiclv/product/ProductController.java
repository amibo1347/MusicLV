package com.musiclv.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int PAGE_SIZE = 12;

    private final ProductService productService;

    /** 상품 목록 + 검색. 헤더 검색창도 이 경로로 붙는다. */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Category category,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "new") String sort,
                       Model model) {

        Sort sorting = switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name" -> Sort.by(Sort.Direction.ASC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Page<Product> products = productService.search(keyword, category,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, sorting));

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", Category.values());
        return "product/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "product/detail";
    }
}
