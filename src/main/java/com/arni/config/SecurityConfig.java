package com.arni.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import com.arni.customService.CustomUserDetailsService;
import com.arni.jwt.JwtAuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService securitycCustomerUserDetailService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            try {
                logger.error("Access denied exception: {}", accessDeniedException.getMessage());
                response.sendRedirect("/access-denied");
            } catch (IOException e) {
                logger.error("Error sending access denied response: {}", e.getMessage());
            }
        };
    }

    @Bean
    public InMemoryTokenRepositoryImpl tokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(securitycCustomerUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        logger.info("DaoAuthenticationProvider configured.");
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        try {
            auth.authenticationProvider(authenticationProvider());
            logger.info("AuthenticationManagerBuilder configured successfully.");
        } catch (Exception e) {
            logger.error("Error configuring AuthenticationManagerBuilder: {}", e.getMessage());
            throw e;
        }
        return auth.build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        try {
            httpSecurity
                .cors().and().csrf().disable()
                .authorizeRequests()
                    .requestMatchers("/login/**", "/public/**", "/auth/**").permitAll() // Public endpoints
                    .requestMatchers("/api/**").authenticated() // Require authentication for this endpoint
                    .anyRequest().authenticated()
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login") // Redirect to login page after logout
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "token")
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            httpSecurity.headers().cacheControl().disable();
            logger.info("SecurityFilterChain configured successfully.");
        } catch (Exception e) {
            logger.error("Error configuring SecurityFilterChain: {}", e.getMessage());
            throw e;
        }
        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("PasswordEncoder (BCryptPasswordEncoder) bean created.");
        return new BCryptPasswordEncoder();
    }

    public void configure(AuthenticationManagerBuilder builder) {
        try {
            builder.eraseCredentials(false);
            logger.info("AuthenticationManagerBuilder credentials erasure setting configured.");
        } catch (Exception e) {
            logger.error("Error configuring AuthenticationManagerBuilder credentials: {}", e.getMessage());
        }
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new SimpleUrlLogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.Authentication authentication) throws IOException, ServletException {
                try {
                    super.onLogoutSuccess(request, response, authentication);
                    logger.info("User logged out successfully.");
                } catch (IOException | ServletException e) {
                    logger.error("Error handling logout success: {}", e);
                }
            }
        };
    }
}
