package com.productRecommendation.utils;

import com.productRecommendation.entity.Product;
import java.util.List;

public class EvaluationUtils {

    public static double calculatePrecision(List<Product> recommendedProducts, List<Product> actualProducts) {
        int numCorrectRecommendations = 0;
        for (Product recommendedProduct : recommendedProducts) {
            if (actualProducts.contains(recommendedProduct)) {
                numCorrectRecommendations++;
            }
        }
        return (double) numCorrectRecommendations / recommendedProducts.size();
    }

    public static double calculateRecall(List<Product> recommendedProducts, List<Product> actualProducts) {
        int numCorrectRecommendations = 0;
        for (Product recommendedProduct : recommendedProducts) {
            if (actualProducts.contains(recommendedProduct)) {
                numCorrectRecommendations++;
            }
        }
        return (double) numCorrectRecommendations / actualProducts.size();
    }

    public static double calculateF1Score(double precision, double recall) {
        if (precision == 0 || recall == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }
}
