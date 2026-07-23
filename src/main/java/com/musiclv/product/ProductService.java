package com.musiclv.product;

import com.musiclv.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    /** 목록 + 검색. keyword 가 비어 있으면 null 로 넘겨 조건에서 제외한다. */
    public Page<Product> search(String keyword, Category category, Pageable pageable) {
        String normalized = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return productRepository.search(normalized, category, pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));
    }

    public List<Product> getLatest() {
        return productRepository.findTop8ByOrderByCreatedAtDesc();
    }

    public long count() {
        return productRepository.count();
    }

    @Transactional
    public Long create(ProductForm form) {
        String imageUrl = fileStorageService.store(form.getImageFile());
        Product product = Product.of(
                form.getName(),
                form.getBrand(),
                form.getCategory(),
                form.getPrice(),
                form.getStock(),
                form.getDescription(),
                imageUrl
        );
        return productRepository.save(product).getId();
    }

    @Transactional
    public void update(Long id, ProductForm form) {
        Product product = getById(id);
        String imageUrl = fileStorageService.store(form.getImageFile());
        product.update(
                form.getName(),
                form.getBrand(),
                form.getCategory(),
                form.getPrice(),
                form.getStock(),
                form.getDescription(),
                imageUrl
        );
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(getById(id));
    }
}
