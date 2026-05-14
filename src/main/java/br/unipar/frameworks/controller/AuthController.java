package br.unipar.frameworks.controller;

import br.unipar.frameworks.config.JwtService;
import br.unipar.frameworks.dto.LoginRequest;
import br.unipar.frameworks.dto.RegisterRequest;
import br.unipar.frameworks.dto.UserResponse;
import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .<ResponseEntity<?>>map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    String token = jwtService.generateToken(userDetails);
                    return ResponseEntity.ok(Map.of(
                            "message", "Login realizado com sucesso",
                            "token", token,
                            "user", new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole())
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of(
                        "error", "Email ou senha inválidos"
                )));
    }
}
