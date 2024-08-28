package com.productRecommendation.controller;

import com.productRecommendation.entity.User;
import com.productRecommendation.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;
    // For login

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register")) // Replace with your registration view name
                .andExpect(model().attributeExists("user")); // Optional: verify user object in model
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        User user = new User("username", "username");

        // Mock userService behavior
        when(userService.save(user)).thenReturn(user);

        mockMvc.perform(post("/register")
                .flashAttr("user", user)) // Flash attribute for data binding
                .andExpect(status().isFound()) // Assuming redirects to login
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testRegisterUser_Error() throws Exception {
        User user = new User("username", "username");

        // Mock userService behavior (throw exception for error simulation)
        when(userService.save(user)).thenThrow(new RuntimeException("Registration failed"));

        mockMvc.perform(post("/register")
                .flashAttr("user", user))
                .andExpect(status().isOk()) // Assuming stays on registration page
                .andExpect(view().name("register")) // Replace with your registration view name
                .andExpect(model().attributeExists("error")); // Verify error message in model
    }

    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login")) // Replace with your login view name
                .andExpect(model().attributeExists("credentials")); // Optional: verify login credentials object
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        String username = "username";
        String password = "password";
        LoginCredentials credentials = new LoginCredentials(username, password); // Replace with your login credential class

        // Mock authentication (adjust based on your implementation)
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))).thenReturn(authentication);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        mockMvc.perform(post("/login")
                .flashAttr("credentials", credentials)
                .with((RequestPostProcessor) securityContext) // Simulate security context for authentication
                .andExpect(status().isFound()) // Assuming redirects to product listing
                .andExpect(redirectedUrl("/products")));
    }

    @Test
    public void testLoginUser_Error() throws Exception {
        String username = "username";
        String password = "invalidPassword";
        LoginCredentials credentials = new LoginCredentials(username, password); // Replace with your login credential class

        // Mock authentication (fail)
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, 
                password))).thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/login")
                .flashAttr("credentials", credentials))
                .andExpect(status().isOk()) // Assuming stays on login page
                .andExpect(view().name("login")) // Replace with your login view name
                .andExpect(model().attributeExists("error")); // Verify error message in model
    }
}
