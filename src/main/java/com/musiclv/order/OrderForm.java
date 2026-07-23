package com.musiclv.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderForm {

    @NotBlank(message = "받는 분 이름을 입력해주세요.")
    @Size(max = 50)
    private String receiverName;

    @NotBlank(message = "연락처를 입력해주세요.")
    @Size(max = 20)
    private String receiverPhone;

    @NotBlank(message = "배송 주소를 입력해주세요.")
    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String memo;
}
