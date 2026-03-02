package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    //injetando a classe service
    @InjectMocks
    PessoaService service; // a classe

    @Mock
    PessoaRepository repository; //simulando o banco de dados

    @Mock
    PessoaMapper mapper; //simulando o conversor


    @Test
    @DisplayName("Deve cadastrar uma pessoa com sucesso")
    void deveCadastrarPessoaComSucesso(){
        //Dado que temos:
        EnderecoRequestDTO enderecoDTO = new EnderecoRequestDTO("Minha rua", "11A", "Meu bairro", "São Paulo", "SP","1111111");
        PessoaRequestDTO dto = new PessoaRequestDTO("Gabriela", LocalDate.of(2010, 10, 21), "111111111", List.of(enderecoDTO));

        Pessoa pessoaMock = new Pessoa();
        pessoaMock.setNome(dto.nome());
        pessoaMock.setEnderecos(new ArrayList<>());

        when(repository.existsByCpf(dto.cpf())).thenReturn(Boolean.FALSE);
        when(mapper.toEntity(dto)).thenReturn(pessoaMock);

        service.cadastrarPessoa(dto);

        verify(repository).save(pessoaMock);
    }

    @Test
    @DisplayName("Não deve cadastrar a pessoa se o cpf existe")
    void nãoDeveCadastrarPessoaComCpfExistente(){
        EnderecoRequestDTO enderecoDTO = new EnderecoRequestDTO("Minha rua", "11A", "Meu bairro", "São Paulo", "SP","1111111");
        PessoaRequestDTO dto = new PessoaRequestDTO("Gabriela", LocalDate.of(2010, 10, 21), "111111111", List.of(enderecoDTO));

        Pessoa pessoaMock = new Pessoa();
        pessoaMock.setNome(dto.nome());
        pessoaMock.setEnderecos(new ArrayList<>());

        when(repository.existsByCpf(dto.cpf())).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(RuntimeException.class, () -> service.cadastrarPessoa(dto));
        verify(repository, never()).save(pessoaMock);
    }
}
