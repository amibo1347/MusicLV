package com.musiclv.admin;

import com.musiclv.product.Category;
import com.musiclv.product.ProductForm;
import com.musiclv.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private static final int PAGE_SIZE = 20;

    private final ProductService productService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Category category,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("products", productService.search(keyword, category,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", Category.values());
        return "admin/product-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("categories", Category.values());
        return "admin/product-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute ProductForm productForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "admin/product-form";
        }
        try {
            productService.create(productForm);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("imageFile", "invalid", e.getMessage());
            model.addAttribute("categories", Category.values());
            return "admin/product-form";
        }
        redirectAttributes.addFlashAttribute("message", "상품이 등록되었습니다.");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("productForm", ProductForm.from(productService.getById(id)));
        model.addAttribute("categories", Category.values());
        return "admin/product-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute ProductForm productForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            return "admin/product-form";
        }
        try {
            productService.update(id, productForm);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("imageFile", "invalid", e.getMessage());
            model.addAttribute("categories", Category.values());
            return "admin/product-form";
        }
        redirectAttributes.addFlashAttribute("message", "상품이 수정되었습니다.");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "주문 내역이 있는 상품은 삭제할 수 없습니다.");
        }
        return "redirect:/admin/products";
    }
}
