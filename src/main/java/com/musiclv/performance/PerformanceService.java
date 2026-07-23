package com.musiclv.performance;

import com.musiclv.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final FileStorageService fileStorageService;

    public Page<Performance> search(String keyword, PerformanceCategory category, Pageable pageable) {
        String normalized = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return performanceRepository.search(normalized, category, pageable);
    }

    public Performance getById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다. id=" + id));
    }

    /** 랜딩에 노출할 공연중/공연예정 */
    public List<Performance> getUpcoming() {
        return performanceRepository.findTop4ByEndDateGreaterThanEqualOrderByStartDateAsc(LocalDate.now());
    }

    public long count() {
        return performanceRepository.count();
    }

    @Transactional
    public Long create(PerformanceForm form) {
        String posterUrl = fileStorageService.store(form.getPosterFile());
        Performance performance = Performance.of(
                form.getTitle(), form.getCategory(), form.getVenue(), form.getCast(),
                form.getStartDate(), form.getEndDate(), form.getRunningTime(), form.getAgeRating(),
                form.getPrice(), form.getSeats(), posterUrl, form.getDescription()
        );
        return performanceRepository.save(performance).getId();
    }

    @Transactional
    public void update(Long id, PerformanceForm form) {
        Performance performance = getById(id);
        String posterUrl = fileStorageService.store(form.getPosterFile());
        performance.update(
                form.getTitle(), form.getCategory(), form.getVenue(), form.getCast(),
                form.getStartDate(), form.getEndDate(), form.getRunningTime(), form.getAgeRating(),
                form.getPrice(), form.getSeats(), posterUrl, form.getDescription()
        );
    }

    @Transactional
    public void delete(Long id) {
        performanceRepository.delete(getById(id));
    }
}
