package com.productRecommendation.controller;

import com.productRecommendation.entity.Product;
import com.productRecommendation.entity.Rating;
import com.productRecommendation.service.ProductService;
import com.productRecommendation.service.RatingsService;
import java.util.List;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.CoreMatchers.hasItems;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private RatingsService ratingsService; // Add for testing rateProduct

    @Test
    public void testShowProducts() throws Exception {
        // Create sample products
        List<Product> products = List.of(new Product(1L),
                new Product(2L)
        );

        // Mock productService behavior
        when(productService.findAll()).thenReturn(products);

        // Perform GET request
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", hasItems(products.get(0), products.get(1))));
    }

    @Test
    public void testShowProductDetails() throws Exception {
        // Create a sample product
        Product product = new Product(1L);

        // Mock productService behavior
        when(productService.findById(1L)).thenReturn(product);

        // Perform GET request with specific product ID
        mockMvc.perform(get("/product/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("productDetails"))
                .andExpect(model().attribute("product", is(product)));
    }

    @Test
    public void testRateProduct() throws Exception {
        // Mock authentication (adjust based on your implementation)
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Mockito.lenient().when(authentication.isAuthenticated()).thenReturn(true);
        Long userId = 1L; // Replace with logic to retrieve user ID

        // Mock productService and ratingsService
        Product product = new Product(1L);
        when(productService.findById(1L)).thenReturn(product);

        // Create rating object
        Rating rating = new Rating(userId, product, 5);

        // Mock ratingsService behavior
        Mockito.doNothing().when(ratingsService).save(rating);

        // Perform POST request
        mockMvc.perform(post("/rateProduct")
                .param("productId", "1")
                .param("rating", "5")
                .with((RequestPostProcessor) securityContext))
                .andExpect(status().isFound()) // Assuming redirects to product details
                .andExpect(redirectedUrl("/product/1")); // Assuming redirect path
    }

    @Test
    public void testSearchProducts_WithKeyword() throws Exception {
        String keyword = "search term";
        List<Product> searchedProducts = List.of(new Product(1L)
        );

        when(productService.findByNameContaining(keyword)).thenReturn(searchedProducts);

        mockMvc.perform(get("/products/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", hasItems(searchedProducts.get(0))))
                .andExpect(model().attribute("keyword", is(keyword))); // Optional: verify keyword persists
    }

    @Test
    public void testSearchProducts_WithoutKeyword() throws Exception {
        // Mock productService behavior (return all products)
        List<Product> allProducts = List.of(new Product(1L),
                new Product(2L)
        );
        when(productService.findAll()).thenReturn(allProducts);

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", hasItems(allProducts.get(0), allProducts.get(1))))
                .andExpect(model().attributeExists("keyword")); // Optional: verify keyword doesn't persist
    }

    @Test
    public void testFilterProducts_ByCategory() throws Exception {
        String category = "Category A";
        List<Product> filteredProducts = List.of(new Product(1L)
        );

        when(productService.findByCategory(category)).thenReturn(filteredProducts);

        mockMvc.perform(get("/products/filter")
                .param("category", category))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", hasItems(filteredProducts.get(0))))
                .andExpect(model().attribute("category", is(category))); // Optional: verify filter persists
    }

    @Test
    public void testFilterProducts_ByPriceRange() throws Exception {
        double minPrice = 10.0;
        double maxPrice = 20.0;
        List<Product> filteredProducts = List.of(new Product(1L)
        );

        when(productService.findByPriceBetween(minPrice, maxPrice)).thenReturn(filteredProducts);

        mockMvc.perform(get("/products/filter")
                .param("minPrice", String.valueOf(minPrice))
                .param("maxPrice", String.valueOf(maxPrice)))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attribute("products", hasItems(filteredProducts.get(0))))
                .andExpect(model().attribute("minPrice", is(minPrice))) // Optional: verify filters persist
                .andExpect(model().attribute("maxPrice", is(maxPrice)));
    }
}
