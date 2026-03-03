package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.EnderecoUpdateRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.exception.CpfExisteException;
import com.gestao.pessoas.mapper.EnderecoMapper;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

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

    @Mock
    EnderecoMapper enderecoMapper;

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
    class ListarPessoas {
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

    @Nested
    @DisplayName("adicionarEndereco")
    class AdicionarEndereco {

        @Test
        @DisplayName("Deve adicionar um endereco novo a uma pessoa")
        void deveAdicionarEnderecoNovoAUmaPessoa() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            EnderecoRequestDTO dto = enderecoSecundario();
            Endereco enderecoMock = Endereco.builder().isPrincipal(false).build();

            when(repository.findById(1L)).thenReturn(Optional.of(pessoaMock));
            when(enderecoMapper.toEntity(dto)).thenReturn(enderecoMock);

            service.adicionarEndereco(1L, dto);

            verify(repository).save(pessoaMock);
            assertThat(pessoaMock.getEnderecos()).contains(enderecoMock);
        }

        @Test
        @DisplayName("Não deve adicionar endereco se pessoa não encontrada")
        void naoDeveAdicionarEnderecoSePessoaNaoEncontrada() {
            EnderecoRequestDTO dto = enderecoSecundario();

            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.adicionarEndereco(1L, dto));
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPessoa")
    class BuscarPessoa {

        @Test
        @DisplayName("Deve buscar uma pessoa com sucesso")
        void deveBuscarPessoaComSucesso() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            PessoaResponseDTO responseMock = pessoaResponseMock(pessoaMock);

            when(repository.findById(1L)).thenReturn(Optional.of(pessoaMock));
            when(mapper.toDTO(pessoaMock)).thenReturn(responseMock);

            PessoaResponseDTO resultado = service.buscarPessoa(1L);

            assertThat(resultado.nome()).isEqualTo(pessoaMock.getNome());
            verify(repository).findById(1L);
            verify(mapper).toDTO(pessoaMock);
        }

        @Test
        @DisplayName("Não deve buscar pessoa se não encontrada")
        void naoDeveBuscarPessoaNaoEncontrada() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.buscarPessoa(99L));
            verify(mapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("atualizarDados")
    class AtualizarDados {

        @Test
        @DisplayName("Deve atualizar dados da pessoa com sucesso")
        void deveAtualizarDadosPessoaComSucesso() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            PessoaUpdateRequestDTO updateDTO = new PessoaUpdateRequestDTO("Gabriela Atualizada", null, null);
            PessoaResponseDTO responseMock = pessoaResponseMock(pessoaMock);

            when(repository.findById(1L)).thenReturn(Optional.of(pessoaMock));
            when(repository.save(pessoaMock)).thenReturn(pessoaMock);
            when(mapper.toDTO(pessoaMock)).thenReturn(responseMock);

            PessoaResponseDTO resultado = service.atualizarDados(1L, updateDTO);

            assertThat(resultado).isNotNull();
            assertThat(pessoaMock.getNome()).isEqualTo("Gabriela Atualizada");
            verify(repository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve atualizar se pessoa não encontrada")
        void naoDeveAtualizarSePessoaNaoEncontrada() {
            PessoaUpdateRequestDTO updateDTO = new PessoaUpdateRequestDTO("Gabriela Atualizada", null, null);

            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.atualizarDados(99L, updateDTO));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Não deve atualizar se endereço não encontrado")
        void naoDeveAtualizarSeEnderecoNaoEncontrado() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            EnderecoUpdateRequestDTO enderecoDTO = new EnderecoUpdateRequestDTO(999L, "Rua Nova", null, null, null, null, null, null);
            PessoaUpdateRequestDTO updateDTO = new PessoaUpdateRequestDTO(null, null, List.of(enderecoDTO));

            when(repository.findById(1L)).thenReturn(Optional.of(pessoaMock));

            assertThrows(EntityNotFoundException.class,
                    () -> service.atualizarDados(1L, updateDTO));
            verify(repository, never()).save(any());
        }
    }
}
