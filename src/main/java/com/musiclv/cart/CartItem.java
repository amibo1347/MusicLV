package com.musiclv.cart;

import com.musiclv.member.Member;
import com.musiclv.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_member_product", columnNames = {"member_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    private CartItem(Member member, Product product, int quantity) {
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public static CartItem of(Member member, Product product, int quantity) {
        return new CartItem(member, product, quantity);
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSubtotal() {
        return product.getPrice() * quantity;
    }
}
