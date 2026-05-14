package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.UserResponse;
import br.unipar.frameworks.model.User;
import br.unipar.frameworks.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<UserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()));
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }

    @GetMapping("/search-safe")
    public List<UserResponse> safeSearch(@RequestParam String term) {
        return userRepository.safeSearchByName(term).stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .toList();
    }

    @GetMapping("/search-unsafe")
    public List<UserResponse> unsafeSearch(@RequestParam String term) {
        String jpql = "select u from User u where lower(u.name) like lower(:term)";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("term", "%" + term + "%");
        
        return query.getResultList().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .toList();
    }
}
