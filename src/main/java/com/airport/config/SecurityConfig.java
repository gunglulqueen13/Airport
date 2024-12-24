package com.airport.config;

import com.airport.service.UserDetailsServiceImpl;
import com.airport.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/login", "/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/manager/stats").hasRole("MANAGER")
                        .requestMatchers("/api/flights/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/api/flights", true)
                        .permitAll()
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Invalid username or password.");
                            response.getWriter().flush();
                        })
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }



    // Обработчик успешной аутентификации
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            // Извлекаем кастомного пользователя из контекста аутентификации
            User user = (User) authentication.getPrincipal();  // Получаем кастомного пользователя
            Long userId = user.getId();  // Получаем user_id

            // Создаем и настраиваем cookie
            Cookie userCookie = new Cookie("user_id", String.valueOf(userId));
            userCookie.setHttpOnly(true);  // Защищаем cookie от доступа через JavaScript
            userCookie.setSecure(true);  // Обеспечиваем безопасность передачи cookie по HTTPS (только для HTTPS)
            userCookie.setMaxAge(3600);  // Устанавливаем время жизни cookie (например, 1 час)
            userCookie.setPath("/");  // Устанавливаем путь, на котором cookie будет доступна

            // Добавляем cookie в ответ
            response.addCookie(userCookie);

            // Перенаправляем на страницу после успешной аутентификации
            response.sendRedirect("/api/flights");
        };
    }
}
