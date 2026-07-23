package com.musiclv.order;

import com.musiclv.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByMemberOrderByIdDesc(Member member);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "member"})
    Optional<Order> findWithItemsById(Long id);

    /** 관리자 주문 관리 — 상태로 걸러본다. null 이면 전체. */
    @Query("""
            select o from Order o join fetch o.member
            where (:status is null or o.status = :status)
            order by o.id desc
            """)
    Page<Order> findForAdmin(@Param("status") OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);
}
