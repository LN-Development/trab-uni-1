package br.unipar.frameworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
    @NotBlank(message = "O nome do produto é obrigatório")
    String name,

    @NotBlank(message = "A descrição do produto é obrigatória")
    String description,

    @NotNull(message = "O preço do produto é obrigatório")
    @Positive(message = "O preço deve ser positivo")
    Double price
) {
}
