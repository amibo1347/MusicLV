package com.musiclv.cart;

import com.musiclv.member.Member;
import com.musiclv.product.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<CartItem> findByMemberOrderByIdAsc(Member member);

    Optional<CartItem> findByMemberAndProduct(Member member, Product product);

    long countByMember(Member member);

    void deleteByMember(Member member);
}
