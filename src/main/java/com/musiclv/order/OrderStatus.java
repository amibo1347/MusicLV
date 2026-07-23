package com.musiclv.order;

public enum OrderStatus {

    ORDERED("주문완료"),
    PAID("결제완료"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 취소 가능한 단계인지 */
    public boolean isCancellable() {
        return this == ORDERED || this == PAID;
    }
}
