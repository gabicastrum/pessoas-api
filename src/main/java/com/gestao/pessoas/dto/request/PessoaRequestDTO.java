package com.gestao.pessoas.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record PessoaRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        LocalDate dataNascimento,

        @NotBlank String cpf,

        @Valid
        @NotEmpty(message = "Pessoa deve ter pelo menos um endereço")
        List<EnderecoRequestDTO> enderecos

) {
}
