package com.productRecommendation.recommendation;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.repository.RatingsRepository;
import com.productRecommendation.service.ProductService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationEngine {

    @Autowired
    private RatingsRepository ratingsRepository;

    @Autowired
    private ProductService productService;

    public List<Product> recommendProducts(Long userId, RecommendationType recommendationType) {
        switch (recommendationType) {
            case COLLABORATIVE_FILTERING:
                return recommendProductsCollaborativeFiltering(userId);
            case CONTENT_BASED_FILTERING:
                return recommendProductsContentBasedFiltering(userId);
            case HYBRID:
                return recommendProductsHybrid(userId, recommendationType);
            default:
                throw new IllegalArgumentException("Invalid recommendation type");
        }
    }

    public List<Product> recommendProductsCollaborativeFiltering(Long userId) {
        // Retrieve user's ratings
        List<Rating> userRatings = ratingsRepository.findByUserId(userId);

        // Create user-product rating matrix
        Map<Long, Map<Long, Integer>> userProductRatings = new HashMap<>();
        for (Rating rating : userRatings) {
            Long userId1 = rating.getUser().getId();
            Long productId = rating.getProduct().getId();
            Integer ratingValue = rating.getRating();
            userProductRatings.computeIfAbsent(userId1, k -> new HashMap<>()).put(productId, ratingValue);
        }

        // Find similar users based on Pearson correlation
        Map<Long, Double> similarUsers = findSimilarUsers(userProductRatings, userId);

        // Recommend products rated highly by similar users
        Map<Product, Double> recommendedProducts = new HashMap<>();
        for (Rating userRating : userRatings) {
            for (Map.Entry<Long, Double> similarUserEntry : similarUsers.entrySet()) {
                Long similarUserId = similarUserEntry.getKey();
                Double similarity = similarUserEntry.getValue();
                List<Rating> similarUserRatings = ratingsRepository.findByUserId(similarUserId);
                for (Rating similarUserRating : similarUserRatings) {
                    if (!similarUserRating.getProduct().getId().equals(userRating.getProduct().getId())) {
                        Product recommendedProduct = similarUserRating.getProduct();
                        recommendedProducts.put(recommendedProduct, recommendedProducts.getOrDefault(recommendedProduct, 0.0) + similarity);
                    }
                }
            }
        }

        // Sort recommended products by similarity scores
        List<Product> sortedRecommendedProducts = new ArrayList<>(recommendedProducts.keySet());
        sortedRecommendedProducts.sort((p1, p2) -> Double.compare(recommendedProducts.get(p2), recommendedProducts.get(p1)));

        return sortedRecommendedProducts;
    }

    public List<Product> recommendProductsContentBasedFiltering(Long userId) {
        // Retrieve user's rated products
        List<Rating> userRatings = ratingsRepository.findByUserId(userId);

        // Extract product features (e.g., keywords, categories)
        Map<Long, Set<String>> productFeatures = new HashMap<>();
        for (Rating rating : userRatings) {
            Product product = rating.getProduct();
            Set<String> features = extractProductFeatures(product);
            productFeatures.put(product.getId(), features);
        }

        // Calculate similarity between user's rated products and other products
        Map<Product, Double> similarityScores = new HashMap<>();
        for (Product product : productService.findAll()) {
            if (!productFeatures.containsKey(product.getId())) {
                continue; // Skip products not rated by the user
            }
            Set<String> productFeatures2 = productFeatures.get(product.getId());
            double similarity = calculateCosineSimilarity(productFeatures.get(userRatings.get(0).getProduct().getId()), productFeatures2);
            similarityScores.put(product, similarity);
        }

        // Sort recommended products by similarity scores
        List<Product> recommendedProducts = new ArrayList<>(similarityScores.keySet());
        recommendedProducts.sort((p1, p2) -> Double.compare(similarityScores.get(p2), similarityScores.get(p1)));

        return recommendedProducts;
    }

    public List<Product> recommendProductsHybrid(Long userId, RecommendationType contentBasedRecommendations) {
        // Get collaborative filtering recommendations
        List<Product> collaborativeFilteringRecommendations = recommendProductsCollaborativeFiltering(userId);

        // Get content-based filtering recommendations
        List<Product> contentBasedFilteringRecommendations = recommendProductsContentBasedFiltering(userId);

        // Combine recommendations (e.g., weighted average)
        Map<Product, Double> combinedScores = new HashMap<>();
        for (Product product : collaborativeFilteringRecommendations) {
            combinedScores.put(product, 0.7 * collaborativeFilteringRecommendations.indexOf(product) + 0.3 * contentBasedFilteringRecommendations.indexOf(product));
        }
        for (Product product : contentBasedFilteringRecommendations) {
            combinedScores.put(product, combinedScores.getOrDefault(product, 0.0) + 0.3 * contentBasedFilteringRecommendations.indexOf(product));
        }

        // Sort recommended products by combined scores
        List<Product> recommendedProducts = new ArrayList<>(combinedScores.keySet());
        recommendedProducts.sort((p1, p2) -> Double.compare(combinedScores.get(p2), combinedScores.get(p1)));

        return recommendedProducts;
    }

    public Set<String> extractProductFeatures(Product product) {
        Set<String> features = new HashSet<>();

        // Tokenize the product description
        List<String> tokens = Arrays.asList(product.getDescription().split("\\s+"));

        // Remove stop words (optional)
        List<String> filteredTokens = tokens.stream()
                .filter(word -> !StopWords.contains(word))
                .collect(Collectors.toList());

        // Add extracted keywords as features
        features.addAll(filteredTokens); // Or use keywords directly
        return features;
    }

    public double calculateCosineSimilarity(Set<String> features1, Set<String> features2) {
        int dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (String feature1 : features1) {
            for (String feature2 : features2) {
                if (feature1.equals(feature2)) {
                    dotProduct++;
                }
            }
            magnitude1 += Math.pow(feature1.length(), 2);
        }

        for (String feature2 : features2) {
            magnitude2 += Math.pow(feature2.length(), 2);
        }

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    public Map<Long, Double> findSimilarUsers(Map<Long, Map<Long, Integer>> userProductRatings, Long userId) {
        Map<Long, Double> similarUsers = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> user1Entry : userProductRatings.entrySet()) {
            Long userId1 = user1Entry.getKey();
            if (userId1.equals(userId)) {
                continue;
            }
            Map<Long, Integer> user1Ratings = user1Entry.getValue();
            Map<Long, Integer> user2Ratings = userProductRatings.get(userId);
            Double similarity = calculatePearsonCorrelation(user1Ratings, user2Ratings);
            if (similarity > 0) {
                similarUsers.put(userId1, similarity);
            }
        }
        return similarUsers;
    }

    public Double calculatePearsonCorrelation(Map<Long, Integer> user1Ratings, Map<Long, Integer> user2Ratings) {
        List<Double> user1RatingsList = new ArrayList<>();
        List<Double> user2RatingsList = new ArrayList<>();
        for (Long productId : user1Ratings.keySet()) {
            if (user2Ratings.containsKey(productId)) {
                user1RatingsList.add((double) user1Ratings.get(productId));
                user2RatingsList.add((double) user2Ratings.get(productId));
            }
        }

        if (user1RatingsList.isEmpty() || user2RatingsList.isEmpty()) {
            return 0.0;
        }

        double sumX = user1RatingsList.stream().mapToDouble(Double::doubleValue).sum();
        double sumY = user2RatingsList.stream().mapToDouble(Double::doubleValue).sum();
        double sumXSq = user1RatingsList.stream().mapToDouble(x -> x * x).sum();
        double sumYSq = user2RatingsList.stream().mapToDouble(y -> y * y).sum();
        double sumXY = user1RatingsList.stream().mapToDouble(x -> x * user2RatingsList.get(user1RatingsList.indexOf(x))).sum();

        double n = user1RatingsList.size();
        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumXSq - sumX * sumX) * (n * sumYSq - sumY * sumY));

        if (denominator == 0) {
            return 0.0;
        }

        return numerator / denominator;
    }
    
    List<Product> combineRecommendations(List<Product> collaborativeRecommendations,
            List<Product> contentBasedRecommendations) {
        Map<Product, Double> combinedScores = new HashMap<>();

        // Assign weights to collaborative and content-based recommendations (adjust as needed)
        double collaborativeWeight = 0.7;
        double contentBasedWeight = 0.3;

        for (Product product : collaborativeRecommendations) {
            combinedScores.put(product, collaborativeWeight * collaborativeRecommendations.indexOf(product));
        }

        for (Product product : contentBasedRecommendations) {
            combinedScores.put(product, combinedScores.getOrDefault(product, 0.0)
                    + contentBasedWeight * contentBasedRecommendations.indexOf(product));
        }

        // Sort recommended products by combined scores
        List<Product> recommendedProducts = new ArrayList<>(combinedScores.keySet());
        recommendedProducts.sort((p1, p2) -> Double.compare(combinedScores.get(p2), combinedScores.get(p1)));

        return recommendedProducts;
    }
}
