package com.gestao.pessoas.dto.response;

public record EnderecoResponseDTO(
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Boolean isPrincipal
) {}
