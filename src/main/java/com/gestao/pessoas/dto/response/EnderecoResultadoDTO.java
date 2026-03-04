package com.gestao.pessoas.dto.response;

public record EnderecoResultadoDTO(
        EnderecoResponseDTO endereco,
        boolean salvo,
        String motivo
) {}