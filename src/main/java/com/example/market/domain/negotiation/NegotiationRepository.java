package com.example.market.domain.negotiation;

import com.example.market.domain.negotiation.Negotiation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NegotiationRepository extends JpaRepository<Negotiation, Long> {

    boolean existsByItemId(Long itemId);

    @Query("select n " +
            "from Negotiation n " +
            "join fetch n.item " +
            "where n.item.id =:itemId")
    Page<Negotiation> findAllByItemId(@Param("itemId") Long itemId, Pageable pageable);

    Optional<Negotiation> findByItemIdAndBuyerId(@Param("itemId") Long itemId, @Param("buyerId") Long buyerId);

    @Modifying(clearAutomatically = true)
    @Query("update Negotiation n set n.status = 'REJECT' where n.id <> :negotiationId and n.item.id = :itemId")
    int updateNegotiationStatus(@Param("negotiationId") Long negotiationId, @Param("itemId") Long itemId);

    Page<Negotiation> findAllBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    Page<Negotiation> findAllByBuyerId(@Param("buyerId") Long buyerId, Pageable pageable);
}
