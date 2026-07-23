package com.musiclv.performance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    /** 공연명·공연장·출연진을 키워드로 검색하고 카테고리로 거른다. */
    @Query("""
            select p from Performance p
            where (:keyword is null
                   or lower(p.title) like lower(concat('%', :keyword, '%'))
                   or lower(p.venue) like lower(concat('%', :keyword, '%'))
                   or lower(p.cast) like lower(concat('%', :keyword, '%')))
              and (:category is null or p.category = :category)
            """)
    Page<Performance> search(@Param("keyword") String keyword,
                             @Param("category") PerformanceCategory category,
                             Pageable pageable);

    /** 랜딩에 올릴 공연중/공연예정 목록 */
    List<Performance> findTop4ByEndDateGreaterThanEqualOrderByStartDateAsc(LocalDate today);
}
