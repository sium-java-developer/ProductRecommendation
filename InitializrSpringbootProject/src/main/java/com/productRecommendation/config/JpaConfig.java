package com.productRecommendation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories(basePackages = "com.productRecommendation.repository") // Set base package for JPA repositories
public class JpaConfig {

    // No additional configuration beans needed for basic JPA setup

}
