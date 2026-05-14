package br.unipar.frameworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
    @NotNull(message = "O ID do produto é obrigatório")
    Long productId,

    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
    String text
) {
}
