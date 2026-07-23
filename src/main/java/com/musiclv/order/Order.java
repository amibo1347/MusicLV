package com.musiclv.order;

import com.musiclv.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
// ORDER 는 SQL 예약어라 테이블명을 orders 로 둔다.
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, length = 50)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 255)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    private Order(Member member, String receiverName, String receiverPhone, String address, String memo) {
        this.member = member;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.memo = memo;
        this.status = OrderStatus.ORDERED;
        this.orderedAt = LocalDateTime.now();
    }

    public static Order create(Member member, String receiverName, String receiverPhone,
                               String address, String memo, List<OrderItem> items) {
        Order order = new Order(member, receiverName, receiverPhone, address, memo);
        for (OrderItem item : items) {
            order.addItem(item);
        }
        return order;
    }

    private void addItem(OrderItem item) {
        orderItems.add(item);
        item.assignOrder(this);
    }

    public int getTotalAmount() {
        return orderItems.stream().mapToInt(OrderItem::getSubtotal).sum();
    }

    public int getTotalQuantity() {
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /** 목록에서 "○○ 외 2건" 으로 보여주기 위한 대표 상품명 */
    public String getSummaryName() {
        if (orderItems.isEmpty()) {
            return "";
        }
        String first = orderItems.get(0).getProduct().getName();
        int rest = orderItems.size() - 1;
        return rest > 0 ? first + " 외 " + rest + "건" : first;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancel() {
        if (!status.isCancellable()) {
            throw new IllegalStateException("이미 " + status.getLabel() + " 상태라 취소할 수 없습니다.");
        }
        orderItems.forEach(OrderItem::cancel);
        this.status = OrderStatus.CANCELLED;
    }
}
