package com.productRecommendation.controller;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.entity.User;
import com.productRecommendation.service.ProductService;
import com.productRecommendation.service.RatingsService;
import com.productRecommendation.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RatingsService ratingsService;

    @Autowired
    private UserService userService;

    @GetMapping("/products")
    public String showProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/product/{productId}")
    public String showProductDetails(@PathVariable Long productId, Model model) {
        Product product = productService.findById(productId);
        List<Rating> ratings = ratingsService.findByProductId(productId);
        double averageRating = ratingsService.findAverageRatingByProductId(productId);
        model.addAttribute("product", product);
        model.addAttribute("ratings", ratings);
        model.addAttribute("averageRating", averageRating);
        return "productDetails";
    }

    @PostMapping("/rateProduct")
    public String rateProduct(@RequestParam Long productId, @RequestParam int rating, @RequestParam Long userId, 
            @RequestParam Product product) {
        // Check if the user is logged in
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            Rating newRating = new Rating(userId, product, 5);
            newRating.setUser(currentUser);
            newRating.setProduct(productService.findById(productId));
            newRating.setRating(rating);
            ratingsService.save(newRating);
        }
        return "redirect:/products";
    }

    @GetMapping("/products/search")
    public String searchProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productService.findByNameContaining(keyword);
        } else {
            products = productService.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword); // Optional: persist search term
        return "products";
    }

    @GetMapping("/products/filter")
    public String filterProducts(@RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {
        List<Product> products = productService.findAll();

        // Apply filters based on provided parameters
        if (category != null && !category.isEmpty()) {
            products = productService.findByCategory(category);
        }
        if (minPrice != null && maxPrice != null) {
            products = productService.findByPriceBetween(minPrice, maxPrice);
        }

        model.addAttribute("products", products);
        model.addAttribute("category", category); // Optional: persist filter selections
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "products";
    }    
}
