package com.productRecommendation.controller;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.entity.User;
import com.productRecommendation.service.RatingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RatingController {

    @Autowired
    private RatingsService ratingsService;

    @Autowired
    private SecurityContext securityContext;

    @PostMapping("/rateProduct")
    public String rateProduct(@RequestParam Long productId, @RequestParam int rating, @RequestParam Product product) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = getUserFromAuthentication(authentication);
            Rating newRating = new Rating(userId, product, 5);
            newRating.setUser(new User("password", "username")); // Assuming userId is accessible from authentication
            newRating.setProduct(new Product(productId)); // Assuming productId is valid
            newRating.setRating(rating);
            ratingsService.save(newRating);
        }

        return "redirect:/product/" + productId; // Redirect back to product details
    }

    private Long getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = ratingsService.findByUsername(userDetails.getUsername()); // Adapt based on user retrieval
            return user.getId();
        }
        return null;
    }
}
