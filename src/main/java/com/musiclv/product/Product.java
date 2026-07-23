package com.musiclv.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 80)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    /** /media/uploads/xxx.jpg 형태의 경로 */
    @Column(length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Product(String name, String brand, Category category, int price, int stock,
                    String description, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public static Product of(String name, String brand, Category category, int price, int stock,
                             String description, String imageUrl) {
        return new Product(name, brand, category, price, stock, description, imageUrl);
    }

    public void update(String name, String brand, Category category, int price, int stock,
                       String description, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
    }

    public boolean isSoldOut() {
        return stock <= 0;
    }

    /** 주문 확정 시 재고 차감 */
    public void reduceStock(int quantity) {
        int remaining = this.stock - quantity;
        if (remaining < 0) {
            throw new IllegalStateException("재고가 부족합니다. 상품: " + name + ", 남은 수량: " + stock);
        }
        this.stock = remaining;
    }

    /** 주문 취소 시 재고 복구 */
    public void restoreStock(int quantity) {
        this.stock += quantity;
    }
}
