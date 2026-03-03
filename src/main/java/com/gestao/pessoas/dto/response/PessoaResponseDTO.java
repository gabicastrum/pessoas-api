package com.gestao.pessoas.dto.response;

import java.util.List;

public record PessoaResponseDTO(
        Long id,
        String nome,
        String cpf,
        List<EnderecoResponseDTO> enderecos
) {
}
