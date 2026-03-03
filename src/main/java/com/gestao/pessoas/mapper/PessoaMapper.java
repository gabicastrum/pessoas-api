package com.gestao.pessoas.mapper;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class})
public interface PessoaMapper {

    @Mapping(target = "id", ignore = true)
    Pessoa toEntity(PessoaRequestDTO dto);

    PessoaResponseDTO toDTO(Pessoa pessoa);
}
