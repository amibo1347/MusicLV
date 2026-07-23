package com.musiclv.order;

import com.musiclv.cart.CartItem;
import com.musiclv.cart.CartService;
import com.musiclv.member.Member;
import com.musiclv.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;

    /** 장바구니 전체를 주문으로 전환하고 장바구니를 비운다. */
    @Transactional
    public Long orderFromCart(Long memberId, OrderForm form) {
        List<CartItem> cartItems = cartService.getItems(memberId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> OrderItem.of(item.getProduct(), item.getQuantity()))
                .toList();

        Long orderId = save(memberId, form, orderItems);
        cartService.clear(memberId);
        return orderId;
    }

    private Long save(Long memberId, OrderForm form, List<OrderItem> items) {
        Member member = memberService.getById(memberId);
        Order order = Order.create(
                member,
                form.getReceiverName(),
                form.getReceiverPhone(),
                form.getAddress(),
                form.getMemo(),
                items
        );
        return orderRepository.save(order).getId();
    }

    public List<Order> getMyOrders(Long memberId) {
        return orderRepository.findByMemberOrderByIdDesc(memberService.getById(memberId));
    }

    /** 본인 주문이거나 관리자일 때만 조회할 수 있다. */
    public Order getOrderFor(Long orderId, Long memberId, boolean admin) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id=" + orderId));
        if (!admin && !order.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 주문이 아닙니다.");
        }
        return order;
    }

    @Transactional
    public void cancel(Long orderId, Long memberId, boolean admin) {
        getOrderFor(orderId, memberId, admin).cancel();
    }

    // ----- 관리자 -----

    public Page<AdminOrderRow> getOrdersForAdmin(OrderStatus status, Pageable pageable) {
        Page<Order> page = orderRepository.findForAdmin(status, pageable);
        // 주문 상품은 지연 로딩이라, 세션이 살아 있는 지금 미리 채운다.
        // default_batch_fetch_size 덕분에 페이지 전체가 몇 번의 쿼리로 함께 로딩된다.
        page.getContent().forEach(order -> order.getOrderItems().forEach(item -> item.getProduct().getName()));
        return page.map(AdminOrderRow::from);
    }

    @Transactional
    public void changeStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id=" + orderId));
        order.changeStatus(status);
    }

    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
}
