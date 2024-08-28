package com.productRecommendation.repository;

import com.productRecommendation.entity.Rating;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface RatingsRepository extends JpaRepository<Rating, Long> {

    // Custom query methods (optional)
    List<Rating> findByUserId(Long userId);
    List<Rating> findByProductId(Long productId);

    // Find the average rating for a product
    @Query("SELECT AVG(rating) FROM Rating WHERE productId = ?1")
    double findAverageRatingByProductId(Long productId);
}
