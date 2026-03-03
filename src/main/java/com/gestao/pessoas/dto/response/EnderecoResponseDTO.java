package com.gestao.pessoas.dto.response;

public record EnderecoResponseDTO(
        Long idEndereco,
        String rua,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String cep,
        Boolean isPrincipal
) {}
