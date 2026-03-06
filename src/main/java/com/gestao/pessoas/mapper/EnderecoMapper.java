package com.gestao.pessoas.mapper;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.response.EnderecoResponseDTO;
import com.gestao.pessoas.dto.response.EnderecoResultadoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "idEndereco", ignore = true)
    @Mapping(target = "pessoa", ignore = true)
    Endereco toEntity(EnderecoRequestDTO dto);

    EnderecoRequestDTO toDto(Endereco endereco);

    EnderecoResponseDTO toDTO(Endereco endereco);
}
