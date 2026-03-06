package com.gestao.pessoas.dto.request;

public record EnderecoRequestDTO(
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Boolean isPrincipal
) {
}
