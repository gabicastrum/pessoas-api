package com.gestao.pessoas.dto.request;

import java.time.LocalDate;
import java.util.List;

public record PessoaUpdateRequestDTO(
        String nome,
        LocalDate dataNascimento,
        List<EnderecoUpdateRequestDTO> enderecos
) {
}
