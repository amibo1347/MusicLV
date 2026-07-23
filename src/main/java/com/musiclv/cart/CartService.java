package com.musiclv.cart;

import com.musiclv.member.Member;
import com.musiclv.member.MemberService;
import com.musiclv.product.Product;
import com.musiclv.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberService memberService;
    private final ProductService productService;

    public List<CartItem> getItems(Long memberId) {
        return cartItemRepository.findByMemberOrderByIdAsc(memberService.getById(memberId));
    }

    public int getTotalAmount(Long memberId) {
        return getItems(memberId).stream().mapToInt(CartItem::getSubtotal).sum();
    }

    public long getItemCount(Long memberId) {
        return cartItemRepository.countByMember(memberService.getById(memberId));
    }

    @Transactional
    public void add(Long memberId, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        Member member = memberService.getById(memberId);
        Product product = productService.getById(productId);

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 남은 수량: " + product.getStock());
        }

        cartItemRepository.findByMemberAndProduct(member, product)
                .ifPresentOrElse(
                        item -> item.addQuantity(quantity),
                        () -> cartItemRepository.save(CartItem.of(member, product, quantity))
                );
    }

    @Transactional
    public void changeQuantity(Long memberId, Long cartItemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        CartItem item = getOwnedItem(memberId, cartItemId);
        if (item.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 남은 수량: " + item.getProduct().getStock());
        }
        item.changeQuantity(quantity);
    }

    @Transactional
    public void remove(Long memberId, Long cartItemId) {
        cartItemRepository.delete(getOwnedItem(memberId, cartItemId));
    }

    @Transactional
    public void clear(Long memberId) {
        cartItemRepository.deleteByMember(memberService.getById(memberId));
    }

    /** 남의 장바구니 항목을 건드리지 못하도록 소유자를 확인한다. */
    private CartItem getOwnedItem(Long memberId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다."));
        if (!item.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 장바구니 항목이 아닙니다.");
        }
        return item;
    }
}
