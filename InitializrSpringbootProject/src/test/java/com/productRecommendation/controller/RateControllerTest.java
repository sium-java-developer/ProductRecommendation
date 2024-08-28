package com.productRecommendation.controller;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.service.RatingsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(RatingController.class)
public class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingsService ratingsService;

    @MockBean
    private SecurityContext securityContext; // For testing authentication

    @Test
    public void testRateProduct() throws Exception {
        Long productId = 1L;
        int rating = 5;

        // Mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);  

        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Long userId = 1L; // Replace with logic to retrieve user ID

        // Mock ratingsService behavior
        Mockito.doNothing().when(ratingsService).save(new Rating(userId, new Product(productId), rating));

        mockMvc.perform(post("/rateProduct")
                .param("productId", String.valueOf(productId))
                .param("rating", String.valueOf(rating))
                .with((RequestPostProcessor) securityContext))
                .andExpect(status().isFound()) // Assuming redirects to product details
                .andExpect(redirectedUrl("/product/" + productId)); // Assuming redirect path
    }
}
