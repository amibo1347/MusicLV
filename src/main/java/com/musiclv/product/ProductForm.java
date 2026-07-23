package com.musiclv.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductForm {

    private Long id;

    @NotBlank(message = "상품명을 입력해주세요.")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "브랜드를 입력해주세요.")
    @Size(max = 80)
    private String brand;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고를 입력해주세요.")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private Integer stock;

    private String description;

    /** 새로 올린 이미지. 비어 있으면 기존 이미지를 유지한다. */
    private MultipartFile imageFile;

    /** 수정 화면에서 현재 이미지를 보여주기 위한 값 */
    private String imageUrl;

    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.id = product.getId();
        form.name = product.getName();
        form.brand = product.getBrand();
        form.category = product.getCategory();
        form.price = product.getPrice();
        form.stock = product.getStock();
        form.description = product.getDescription();
        form.imageUrl = product.getImageUrl();
        return form;
    }
}
