package com.productRecommendation.service;

import com.productRecommendation.entity.Rating;
import com.productRecommendation.entity.User;
import com.productRecommendation.repository.RatingsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RatingsService {

    @Autowired
    private RatingsRepository ratingsRepository;

    public List<Rating> findByUserId(Long userId) {
        return ratingsRepository.findByUserId(userId);
    }

    public List<Rating> findByProductId(Long productId) {
        return ratingsRepository.findByProductId(productId);
    }

    public double findAverageRatingByProductId(Long productId) {
        return ratingsRepository.findAverageRatingByProductId(productId);
    }

    public Rating save(Rating rating) {
        return ratingsRepository.save(rating);
    }

    public void delete(Long id) {
        ratingsRepository.deleteById(id);
    }

    public Object findByProductIdAndUserId(Long productId, Long userId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public User findByUsername(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
