package com.productRecommendation.recommendation;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.repository.RatingsRepository;
import com.productRecommendation.service.ProductService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationEngine recommendationEngine;

    @Mock
    private RatingsRepository ratingsRepository;

    @Mock
    private ProductService productService;

    @Test
    public void testRecommendProductsCollaborativeFiltering() throws Exception {
        Long userId = 1L;

        // Mock behavior for collaborative filtering
        List<Product> recommendedProducts = List.of(new Product(2L, "Product B", "", 0.0, ""));
        when(recommendationEngine.recommendProductsCollaborativeFiltering(userId)).thenReturn(recommendedProducts);

        List<Product> recommendations = recommendationEngine.recommendProducts(userId, RecommendationType.COLLABORATIVE_FILTERING);

        assertEquals(recommendations, recommendedProducts);
    }

    @Test
    public void testRecommendProductsContentBasedFiltering() throws Exception {
        Long userId = 1L;
        Product userRatedProduct = new Product(1L, "Product A", "feature1", 10.0, "categoryA");
        Product recommendedProduct = new Product(2L, "Product B", "feature1,feature2", 20.0, "categoryB");

        // Mock behavior for user ratings and product service
        List<Rating> userRatings = List.of(new Rating(userId, userRatedProduct, 5));
        when(ratingsRepository.findByUserId(userId)).thenReturn(userRatings);
        when(productService.findAll()).thenReturn(List.of(userRatedProduct, recommendedProduct));

        // Mock feature extraction and similarity calculation
        Set<String> userRatedProductFeatures = Set.of("feature1");
        Set<String> recommendedProductFeatures = Set.of("feature1", "feature2");
        when(recommendationEngine.extractProductFeatures(userRatedProduct)).thenReturn(userRatedProductFeatures);
        when(recommendationEngine.extractProductFeatures(recommendedProduct)).thenReturn(recommendedProductFeatures);
        when(recommendationEngine.calculateCosineSimilarity(userRatedProductFeatures, recommendedProductFeatures)).thenReturn(0.8);

        List<Product> recommendations = recommendationEngine.recommendProducts(userId, RecommendationType.CONTENT_BASED_FILTERING);

        // Assert that recommended product is at the top with highest similarity score
        assertEquals(recommendedProduct, recommendations.get(0));
    }

    @Test
    public void testRecommendProducts_Hybrid() throws Exception {
        Long userId = 1L;

        // Mock behavior for collaborative and content-based filtering
        List<Product> collaborativeRecommendations = List.of(new Product(2L, "Product B (Collab)", "", 0.0, ""));
        List<Product> contentBasedRecommendations = List.of(new Product(3L, "Product C (Content)", "", 0.0, ""));
        when(recommendationEngine.recommendProductsCollaborativeFiltering(userId)).thenReturn(collaborativeRecommendations);
        when(recommendationEngine.recommendProductsContentBasedFiltering(userId)).thenReturn(contentBasedRecommendations);

        // Mock hybrid recommendation logic
        List<Product> expectedRecommendations = List.of(collaborativeRecommendations.get(0), contentBasedRecommendations.get(0));
        when(recommendationEngine.combineRecommendations(collaborativeRecommendations, contentBasedRecommendations)).thenReturn(expectedRecommendations);

        List<Product> recommendations = recommendationEngine.recommendProducts(userId, RecommendationType.HYBRID);

        assertEquals(expectedRecommendations, recommendations);
    }

}
