package com.airport.api;

import com.airport.domain.Role;
import com.airport.domain.RoleType;
import com.airport.domain.User;
import com.airport.persistence.RoleRepository;
import com.airport.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Проверяем, существует ли уже пользователь с таким логином
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            // Возвращаем ошибку, если такой пользователь уже существует
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
        }

        // Кодируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Получаем роль USER по умолчанию
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User Role not set."));

        // Назначаем роль пользователю
        user.setRoles(Collections.singleton(userRole));

        // Сохраняем пользователя в базе данных
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}
