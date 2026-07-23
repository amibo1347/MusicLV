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

    /**
     * 고객에게 보여주는 주문번호. 비회원이 나중에 주문을 찾을 때 쓰는 열쇠라 추측이 어려워야 한다.
     * (기존 주문 데이터를 위해 DB 제약은 unique 만 두고, 값은 항상 코드에서 채운다)
     */
    @Column(unique = true, length = 30)
    private String orderNumber;

    /** 비회원 주문이면 null 이다. */
    @ManyToOne(fetch = FetchType.LAZY)
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

    private Order(String orderNumber, Member member, String receiverName, String receiverPhone,
                  String address, String memo) {
        this.orderNumber = orderNumber;
        this.member = member;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.memo = memo;
        this.status = OrderStatus.ORDERED;
        this.orderedAt = LocalDateTime.now();
    }

    /** 회원 주문. member 가 null 이면 비회원 주문이 된다. */
    public static Order create(String orderNumber, Member member, String receiverName, String receiverPhone,
                               String address, String memo, List<OrderItem> items) {
        Order order = new Order(orderNumber, member, receiverName, receiverPhone, address, memo);
        for (OrderItem item : items) {
            order.addItem(item);
        }
        return order;
    }

    public boolean isGuestOrder() {
        return member == null;
    }

    /** 주문자 이름 — 비회원이면 받는 분 이름을 그대로 쓴다. */
    public String getOrdererName() {
        return member != null ? member.getName() : receiverName;
    }

    public String getOrdererEmail() {
        return member != null ? member.getEmail() : "비회원";
    }

    /** 비회원 주문 조회 시 연락처가 맞는지 확인한다. 하이픈 유무는 무시한다. */
    public boolean matchesPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return normalizePhone(receiverPhone).equals(normalizePhone(phone));
    }

    private static String normalizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("[^0-9]", "");
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
