package com.musiclv.booking;

import com.musiclv.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = "performance")
    List<Booking> findByMemberOrderByIdDesc(Member member);

    @EntityGraph(attributePaths = {"performance", "member"})
    Optional<Booking> findWithDetailById(Long id);

    @Query("""
            select b from Booking b join fetch b.member join fetch b.performance
            where (:status is null or b.status = :status)
            order by b.id desc
            """)
    Page<Booking> findForAdmin(@Param("status") BookingStatus status, Pageable pageable);
}
