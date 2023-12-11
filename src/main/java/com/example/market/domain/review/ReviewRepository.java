package com.example.market.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByItemIdAndReviewerIdAndRevieweeId(@Param("itemId") Long itemId,
                                                     @Param("reviewerId") Long reviewerId,
                                                     @Param("revieweeId") Long revieweeId);
}
