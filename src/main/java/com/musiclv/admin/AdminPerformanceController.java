package com.musiclv.admin;

import com.musiclv.performance.PerformanceCategory;
import com.musiclv.performance.PerformanceForm;
import com.musiclv.performance.PerformanceService;
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
@RequestMapping("/admin/performances")
@RequiredArgsConstructor
public class AdminPerformanceController {

    private static final int PAGE_SIZE = 20;

    private final PerformanceService performanceService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) PerformanceCategory category,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        model.addAttribute("performances", performanceService.search(keyword, category,
                PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", PerformanceCategory.values());
        return "admin/performance-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("performanceForm", new PerformanceForm());
        model.addAttribute("categories", PerformanceCategory.values());
        return "admin/performance-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute PerformanceForm performanceForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validatePeriod(performanceForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", PerformanceCategory.values());
            return "admin/performance-form";
        }
        try {
            performanceService.create(performanceForm);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("posterFile", "invalid", e.getMessage());
            model.addAttribute("categories", PerformanceCategory.values());
            return "admin/performance-form";
        }
        redirectAttributes.addFlashAttribute("message", "공연이 등록되었습니다.");
        return "redirect:/admin/performances";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("performanceForm", PerformanceForm.from(performanceService.getById(id)));
        model.addAttribute("categories", PerformanceCategory.values());
        return "admin/performance-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute PerformanceForm performanceForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validatePeriod(performanceForm, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", PerformanceCategory.values());
            return "admin/performance-form";
        }
        try {
            performanceService.update(id, performanceForm);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("posterFile", "invalid", e.getMessage());
            model.addAttribute("categories", PerformanceCategory.values());
            return "admin/performance-form";
        }
        redirectAttributes.addFlashAttribute("message", "공연이 수정되었습니다.");
        return "redirect:/admin/performances";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            performanceService.delete(id);
            redirectAttributes.addFlashAttribute("message", "공연이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "예매 내역이 있는 공연은 삭제할 수 없습니다.");
        }
        return "redirect:/admin/performances";
    }

    private void validatePeriod(PerformanceForm form, BindingResult bindingResult) {
        if (!form.isPeriodValid()) {
            bindingResult.rejectValue("endDate", "invalid", "종료일은 시작일보다 빠를 수 없습니다.");
        }
    }
}
