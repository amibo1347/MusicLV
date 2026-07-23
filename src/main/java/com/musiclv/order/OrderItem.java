package com.musiclv.order;

import com.musiclv.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    /** 주문 시점의 가격을 박제한다. 이후 상품 가격이 바뀌어도 주문 내역은 유지된다. */
    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private int quantity;

    private OrderItem(Product product, int orderPrice, int quantity) {
        this.product = product;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    public static OrderItem of(Product product, int quantity) {
        product.reduceStock(quantity);
        return new OrderItem(product, product.getPrice(), quantity);
    }

    void assignOrder(Order order) {
        this.order = order;
    }

    public int getSubtotal() {
        return orderPrice * quantity;
    }

    void cancel() {
        product.restoreStock(quantity);
    }
}
