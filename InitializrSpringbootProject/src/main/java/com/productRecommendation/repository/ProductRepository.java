package com.productRecommendation.repository;

import com.productRecommendation.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query methods
    List<Product> findByCategory(String category);
    List<Product> findTop10ByRatingDesc();

    // Find products by name containing a given keyword
    List<Product> findByNameContaining(String keyword);

    // Find products by price range
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Count the number of products
    long count();

    // Delete all products
    void deleteAll();
}
