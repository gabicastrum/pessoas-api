package com.gestao.pessoas.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record PessoaRequestDTO(
        @NotBlank String nome,
        LocalDate dataNascimento,
        @NotBlank String cpf,
        @NotEmpty List<EnderecoRequestDTO> enderecos

) {
}
