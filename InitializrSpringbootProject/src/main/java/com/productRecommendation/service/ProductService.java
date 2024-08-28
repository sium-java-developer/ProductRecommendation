package com.productRecommendation.service;

import com.productRecommendation.entity.Product;
import com.productRecommendation.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return (Product) productRepository.findById(id).orElse(null);

    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findTop10ByRatingDesc() {
        return productRepository.findTop10ByRatingDesc();
    }

    public List<Product> findByNameContaining(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }

    public List<Product> findByPriceBetween(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public long count() {
        return productRepository.count();
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }
}
