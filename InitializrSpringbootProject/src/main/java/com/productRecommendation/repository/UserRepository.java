package com.productRecommendation.repository;

import com.productRecommendation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query methods (optional)
    User findByUsername(String username);
    User findByEmail(String email);
}
