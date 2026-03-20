package com.gestao.pessoas.dto.request;

import jakarta.validation.constraints.Pattern;

public record EnderecoRequestDTO(
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,

        @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
        String cep,
        Boolean isPrincipal
) {
}
