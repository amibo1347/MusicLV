package com.musiclv.order;

import java.time.LocalDateTime;

/**
 * 관리자 주문 목록의 한 줄.
 * open-in-view=false 라서 뷰에서는 지연 로딩이 불가능하다.
 * 트랜잭션 안에서 필요한 값만 뽑아 이 레코드로 옮긴 뒤 화면에 넘긴다.
 */
public record AdminOrderRow(
        Long id,
        LocalDateTime orderedAt,
        String memberName,
        String memberEmail,
        String summaryName,
        int totalQuantity,
        int totalAmount,
        OrderStatus status
) {
    public static AdminOrderRow from(Order order) {
        return new AdminOrderRow(
                order.getId(),
                order.getOrderedAt(),
                order.getMember().getName(),
                order.getMember().getEmail(),
                order.getSummaryName(),
                order.getTotalQuantity(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }
}
