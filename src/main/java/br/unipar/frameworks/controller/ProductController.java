package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.ProductRequest;
import br.unipar.frameworks.dto.ProductResponse;
import br.unipar.frameworks.model.Product;
import br.unipar.frameworks.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Page<ProductResponse> listProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest req) {
        Product p = new Product(null, req.name(), req.description(), req.price());
        return ResponseEntity.ok(toResponse(productRepository.save(p)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        Product p = productRepository.findById(id).orElseThrow();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        return ResponseEntity.ok(toResponse(productRepository.save(p)));
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice());
    }
}
