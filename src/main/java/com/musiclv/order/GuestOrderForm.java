package com.musiclv.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 비회원 주문서. 장바구니 없이 상품 하나를 바로 주문한다.
 */
@Getter
@Setter
public class GuestOrderForm {

    @NotNull(message = "상품 정보가 올바르지 않습니다.")
    private Long productId;

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity = 1;

    @NotBlank(message = "받는 분 이름을 입력해주세요.")
    @Size(max = 50)
    private String receiverName;

    @NotBlank(message = "연락처를 입력해주세요.")
    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
             message = "연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String receiverPhone;

    @NotBlank(message = "배송 주소를 입력해주세요.")
    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String memo;

    /** 주문번호를 잃어버리지 않도록 안내를 확인했는지 */
    private boolean agreed;

    public OrderForm toOrderForm() {
        OrderForm form = new OrderForm();
        form.setReceiverName(receiverName);
        form.setReceiverPhone(receiverPhone);
        form.setAddress(address);
        form.setMemo(memo);
        return form;
    }
}
