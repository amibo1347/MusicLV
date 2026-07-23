package com.musiclv.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 키워드(상품명 또는 브랜드)와 카테고리를 조합한 검색.
     * 둘 다 null 이면 전체 목록이 된다.
     */
    @Query("""
            select p from Product p
            where (:keyword is null
                   or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(p.brand) like lower(concat('%', :keyword, '%')))
              and (:category is null or p.category = :category)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("category") Category category,
                         Pageable pageable);

    List<Product> findTop8ByOrderByCreatedAtDesc();

    long countByCategory(Category category);
}
