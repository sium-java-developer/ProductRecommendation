package com.productRecommendation.utils;


import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecommendationUtils {

    public static List<Product> filterProductsByCategory(List<Product> products, String category) {
        return products.stream()
                .filter(product -> product.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
    public static List<Product> sortProductsByRating(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getRating).reversed())
                .collect(Collectors.toList());
    }
**/
    public static List<Product> recommendSimilarProducts(Product product, List<Product> allProducts, List<Rating> ratings) {
        // Collaborative Filtering: Find users who rated the given product similarly
        Map<Long, Integer> similarUserRatings = new HashMap<>();
        for (Rating rating : ratings) {
            if (rating.getProduct().getId().equals(product.getId())) {
                similarUserRatings.put(rating.getUser().getId(), rating.getRating());
            }
        }

        // Find products rated by similar users
        Map<Long, Integer> recommendedProductRatings = new HashMap<>();
        for (Rating rating : ratings) {
            if (similarUserRatings.containsKey(rating.getUser().getId())) {
                recommendedProductRatings.put(rating.getProduct().getId(), rating.getRating());
            }
        }

        // Calculate similarity scores for recommended products
        Map<Product, Double> similarityScores = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : recommendedProductRatings.entrySet()) {
            Product recommendedProduct = allProducts.stream()
                    .filter(p -> p.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);
            if (recommendedProduct != null) {
                double similarityScore = calculateSimilarity(similarUserRatings.get(product.getId()), entry.getValue());
                similarityScores.put(recommendedProduct, similarityScore);
            }
        }

        // Sort recommended products by similarity scores
        List<Product> recommendedProducts = new ArrayList<>(similarityScores.keySet());
        recommendedProducts.sort((p1, p2) -> Double.compare(similarityScores.get(p2), similarityScores.get(p1)));

        return recommendedProducts;
    }

    private static double calculateSimilarity(int rating1, int rating2) {
        // Simple cosine similarity calculation
        return (double) (rating1 * rating2) / (Math.sqrt(rating1 * rating1) * Math.sqrt(rating2 * rating2));
    }
}
