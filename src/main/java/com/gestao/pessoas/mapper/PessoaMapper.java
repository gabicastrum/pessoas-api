package com.gestao.pessoas.mapper;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class})
public interface PessoaMapper {

    @Mapping(target = "id", ignore = true)
    Pessoa toEntity(PessoaRequestDTO dto);

    @Mapping(target = "idade", source = "dataNascimento", qualifiedByName = "calcularIdade")
    PessoaResponseDTO toDTO(Pessoa pessoa);

    @Named("calcularIdade")
    static Integer calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return null;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}
