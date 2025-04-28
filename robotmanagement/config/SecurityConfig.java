package com.example.robotmanagement.config;

import com.example.robotmanagement.security.JwtAuthenticationFilter;
import com.example.robotmanagement.service.CustomUserDetailsService;
import com.example.robotmanagement.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())  // ✅ Enable CORS with your CorsConfig
                .csrf(AbstractHttpConfigurer::disable)
                // ✅ Add this session policy here:
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws-login-activity", "/api/auth/**").permitAll()
                        .requestMatchers("/api/upload").authenticated()
                        .requestMatchers("/api/user", "/api/user/**", "/api/admin/export/xml").hasAuthority("ADMIN")
                        .requestMatchers("/api/robots/**", "/api/tasks/**", "/api/tasks/simulate/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/api/robots/get/**","/api/robots/update/**", "/api/tasks/get/**").hasAnyAuthority("USER", "ADMIN")  // Allow USER or ADMIN role for robots endpoint
                        .requestMatchers("/topic/login-activity/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )// Add your custom JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);;
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);  // Ensure you inject the correct dependencies
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(provider));
    }
}
