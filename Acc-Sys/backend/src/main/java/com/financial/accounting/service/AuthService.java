package com.financial.accounting.service;

import com.financial.accounting.entity.User;
import com.financial.accounting.repository.UserRepository;
import com.financial.accounting.security.JwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@Service
public class AuthService implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByUsername("admin")) {
            users.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN"));
        }
    }

    public Map<String, String> login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return Map.of("token", jwtService.createToken(username), "username", username);
    }

    public Map<String, String> register(String username, String password) {
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        }
        User user = users.save(new User(username, passwordEncoder.encode(password), "USER"));
        return Map.of("token", jwtService.createToken(user.getUsername()), "username", user.getUsername());
    }
}
