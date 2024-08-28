package com.productRecommendation.utils;

import com.productRecommendation.entity.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DataPreprocessingUtils {

    public static List<Double> normalizeRatings(List<Double> ratings) {
        // Find the minimum and maximum rating values
        double minRating = ratings.stream().min(Double::compare).orElse(0.0);
        double maxRating = ratings.stream().max(Double::compare).orElse(5.0);

        // Normalize ratings to a range between 0 and 1
        return ratings.stream()
                .map(rating -> (rating - minRating) / (maxRating - minRating))
                .collect(Collectors.toList());
    }

    public static List<Integer> handleMissingRatings(List<Integer> ratings, int defaultValue) {
        return ratings.stream()
                .map(rating -> rating == 0 ? defaultValue : rating)
                .collect(Collectors.toList());
    }
    
    public static List<String> convertCategoriesToNumerical(List<Product> products) {
        // Create a mapping between category names and unique numerical IDs
        Map<String, Integer> categoryMapping = new HashMap<>();
        int categoryId = 0;
        for (Product product : products) {
            if (!categoryMapping.containsKey(product.getCategory())) {
                categoryMapping.put(product.getCategory(), categoryId++);
            }
            product.setCategoryId(categoryMapping.get(product.getCategory()));
        }

        // Return a list of category names for reference
        return new ArrayList<>(categoryMapping.keySet());
    }
}
