package com.productRecommendation.service;

import com.productRecommendation.entity.User;
import com.productRecommendation.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);  

    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);  

    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId); 
    }

    public User getCurrentUser() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
