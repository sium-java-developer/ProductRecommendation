package com.productRecommendation.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();  
        resolver.setSuffix(".html"); // Set suffix for Thymeleaf templates
        return resolver;
    }

    // Enable CORS (optional)
    public void addCorsMappings(CorsConfigurationSource source) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080")); // Replace with allowed origins
        config.setAllowedMethods(List.of("GET", "POST")); // Allowed HTTP methods
        config.setAllowedHeaders(List.of("*")); // Allowed headers (adjust as needed)
        config.setAllowCredentials(true); // Allow cookies for CORS requests
        // source.registerCorsConfiguration("/**", config); // Apply CORS to all paths
    }
}
