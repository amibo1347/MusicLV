package com.musiclv.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** 비회원 주문 조회 — 주문번호와 연락처가 모두 맞아야 보여준다. */
@Getter
@Setter
public class GuestLookupForm {

    @NotBlank(message = "주문번호를 입력해주세요.")
    private String orderNumber;

    @NotBlank(message = "주문 시 입력한 연락처를 입력해주세요.")
    private String phone;
}
