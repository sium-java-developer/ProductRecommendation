package com.productRecommendation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    // Inject UserDetailsService

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/public/**").permitAll() // Allow access to public resources
                .antMatchers("/admin/**").hasRole("ADMIN") // Restrict admin area to ADMIN role
                .anyRequest().authenticated() // Require authentication for other requests
                .and()
                .formLogin()
                .loginPage("/login") // Login page endpoint
                .defaultSuccessUrl("/") // Redirect to homepage on successful login
                .permitAll() // Allow access to login page
                .and()
                .logout()
                .logoutUrl("/logout") // Logout endpoint
                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                .permitAll()
                .and()
                .exceptionHandling() // Configure exception handling
                .accessDeniedPage("/accessDenied"); // Redirect to access denied page for unauthorized access
        
        http
                .csrf().disable(); // Disable CSRF for simplicity (consider enabling in production)
        // You can enable CSRF protection with CSRFConfigurer if needed
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                // Use injected UserDetailsService
                .passwordEncoder(passwordEncoder()); // Apply password encoder
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt password encoder
    }
}
