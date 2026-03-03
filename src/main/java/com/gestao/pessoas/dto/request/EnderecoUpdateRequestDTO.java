package com.gestao.pessoas.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnderecoUpdateRequestDTO(
        @NotNull Long idEndereco,
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Boolean isPrincipal
) {
}
