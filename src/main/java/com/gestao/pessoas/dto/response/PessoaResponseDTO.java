package com.gestao.pessoas.dto.response;

import java.time.LocalDate;
import java.util.List;

public record PessoaResponseDTO(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        Integer idade,
        List<EnderecoResponseDTO> enderecos
) {
}
