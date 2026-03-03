package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.exception.CpfExisteException;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.gestao.pessoas.builder.PessoaTestBuilder.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    @InjectMocks
    PessoaService service;

    @Mock
    PessoaRepository repository;

    @Mock
    PessoaMapper mapper;

    @Nested
    @DisplayName("cadastrarPessoa")
    class CadastrarPessoa {

        @Test
        @DisplayName("Deve cadastrar uma pessoa com sucesso")
        void deveCadastrarPessoaComSucesso(){
            PessoaRequestDTO dto = pessoaComUmEndereco();
            Pessoa pessoaMock = pessoaMock(dto);

            when(repository.existsByCpf(dto.cpf())).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(repository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve cadastrar a pessoa se o cpf existe")
        void nãoDeveCadastrarPessoaComCpfExistente(){
            PessoaRequestDTO dto = pessoaComUmEndereco();

            when(repository.existsByCpf(dto.cpf())).thenReturn(true);

            assertThrows(CpfExisteException.class, () -> service.cadastrarPessoa(dto));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve cadastrar uma pessoa com 3 endereços, um principal, com sucesso")
        void deveCadastrarPessoaComTresEnderecos(){
            PessoaRequestDTO dto = pessoaComTresEnderecos();
            Pessoa pessoaMock = pessoaMock(dto);

            when(repository.existsByCpf(dto.cpf())).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(repository).save(pessoaMock);
        }

        @Test
        @DisplayName("Deve cadastrar uma pessoa sem endereco com sucesso")
        void deveCadastrarPessoaSemEndereco(){
            PessoaRequestDTO dto = pessoaSemEnderecos();
            Pessoa pessoaMock = pessoaMock(dto);

            when(repository.existsByCpf(dto.cpf())).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(repository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve cadastrar uma pessoa com dois enderecos principais")
        void naoDeveCadastrarPessoaComDoisEnderecosPrincipais(){
            PessoaRequestDTO dto = pessoaComDoisPrincipais();
            Pessoa pessoaMock = pessoaMockComDoisPrincipais(dto);

            Endereco e1 = new Endereco(); e1.setIsPrincipal(true);
            Endereco e2 = new Endereco(); e2.setIsPrincipal(true);
            pessoaMock.setEnderecos(new ArrayList<>(List.of(e1, e2)));

            when(repository.existsByCpf(dto.cpf())).thenReturn(false);
            when(mapper.toEntity(dto)).thenReturn(pessoaMock);

            assertThrows(IllegalArgumentException.class, () -> service.cadastrarPessoa(dto));
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listarPessoas")
    class listarPessoas {
        @Test
        @DisplayName("Deve retornar uma lista de pessoas e seus endereços")
        void deveRetornarTodasPessoas() {
            Pageable pageable = PageRequest.of(0, 10);
            Pessoa pessoaMock = pessoaMock(pessoaComTresEnderecos());
            PessoaResponseDTO responseMock = pessoaResponseMock(pessoaMock);

            Page<Pessoa> paginaMock = new PageImpl<>(List.of(pessoaMock));

            when(repository.findAll(pageable)).thenReturn(paginaMock);
            when(mapper.toDTO(pessoaMock)).thenReturn(responseMock);

            Page<PessoaResponseDTO> resultado = service.listarPessoas(pageable);

            assertThat(resultado.getContent())
                    .hasSize(1)
                    .first()
                    .extracting(PessoaResponseDTO::nome)
                    .isEqualTo(pessoaMock.getNome());

            verify(repository).findAll(pageable);
            verify(mapper).toDTO(pessoaMock);
        }
    }
}
