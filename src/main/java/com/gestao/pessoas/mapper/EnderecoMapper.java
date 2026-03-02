package com.gestao.pessoas.mapper;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pessoa", ignore = true)
    Endereco toEntity(EnderecoRequestDTO dto);
}
