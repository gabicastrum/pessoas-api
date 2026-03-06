package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.EnderecoUpdateRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import com.gestao.pessoas.dto.response.EnderecoResultadoDTO;
import com.gestao.pessoas.dto.response.PageResponseDTO;
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
    PessoaRepository pessoaRepository;

    @Mock
    PessoaMapper pessoaMapper;

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

            when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(false);
            when(pessoaMapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(pessoaRepository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve cadastrar a pessoa se o cpf existe")
        void nãoDeveCadastrarPessoaComCpfExistente(){
            PessoaRequestDTO dto = pessoaComUmEndereco();

            when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(true);

            assertThrows(CpfExisteException.class, () -> service.cadastrarPessoa(dto));
            verify(pessoaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve cadastrar uma pessoa com 3 endereços, um principal, com sucesso")
        void deveCadastrarPessoaComTresEnderecos(){
            PessoaRequestDTO dto = pessoaComTresEnderecos();
            Pessoa pessoaMock = pessoaMock(dto);

            when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(false);
            when(pessoaMapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(pessoaRepository).save(pessoaMock);
        }

        @Test
        @DisplayName("Deve cadastrar uma pessoa sem endereco com sucesso")
        void deveCadastrarPessoaSemEndereco(){
            PessoaRequestDTO dto = pessoaSemEnderecos();
            Pessoa pessoaMock = pessoaMock(dto);

            when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(false);
            when(pessoaMapper.toEntity(dto)).thenReturn(pessoaMock);

            service.cadastrarPessoa(dto);

            verify(pessoaRepository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve cadastrar uma pessoa com dois enderecos principais")
        void naoDeveCadastrarPessoaComDoisEnderecosPrincipais(){
            PessoaRequestDTO dto = pessoaComDoisPrincipais();
            Pessoa pessoaMock = pessoaMockComDoisPrincipais(dto);

            Endereco e1 = new Endereco(); e1.setIsPrincipal(true);
            Endereco e2 = new Endereco(); e2.setIsPrincipal(true);
            pessoaMock.setEnderecos(new ArrayList<>(List.of(e1, e2)));

            when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(false);
            when(pessoaMapper.toEntity(dto)).thenReturn(pessoaMock);

            assertThrows(IllegalArgumentException.class, () -> service.cadastrarPessoa(dto));
            verify(pessoaRepository, never()).save(any());
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

            when(pessoaRepository.findAll(pageable)).thenReturn(paginaMock);
            when(pessoaMapper.toDTO(pessoaMock)).thenReturn(responseMock);

            PageResponseDTO<PessoaResponseDTO> resultado = service.listarPessoas(pageable);

            assertThat(resultado.content())
                    .hasSize(1)
                    .first()
                    .extracting(PessoaResponseDTO::nome)
                    .isEqualTo(pessoaMock.getNome());

            verify(pessoaRepository).findAll(pageable);
            verify(pessoaMapper).toDTO(pessoaMock);
        }
    }

    @Nested
    @DisplayName("adicionarEndereco")
    class AdicionarEndereco {

        @Test
        @DisplayName("Deve adicionar um endereço secundário com sucesso")
        void deveAdicionarEnderecoComSucesso() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            EnderecoRequestDTO dto = enderecoSecundario();
            Endereco enderecoMock = Endereco.builder().idEndereco(2L).isPrincipal(false).build();

            when(pessoaRepository.findByIdComEnderecos(1L)).thenReturn(Optional.of(pessoaMock));
            when(enderecoMapper.toEntity(dto)).thenReturn(enderecoMock);
            when(enderecoMapper.toDTO(enderecoMock)).thenReturn(enderecoResponseMock(enderecoMock));

            List<EnderecoResultadoDTO> resultados = service.adicionarEndereco(1L, List.of(dto));

            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).salvo()).isTrue();
            verify(pessoaRepository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve adicionar endereço principal se já existe um")
        void naoDeveAdicionarEnderecoPrincipalSeJaExisteUm() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            EnderecoRequestDTO dto = enderecoPrincipal();
            Endereco enderecoMock = Endereco.builder().idEndereco(2L).isPrincipal(true).build();

            when(pessoaRepository.findByIdComEnderecos(1L)).thenReturn(Optional.of(pessoaMock));
            when(enderecoMapper.toEntity(dto)).thenReturn(enderecoMock);
            when(enderecoMapper.toDTO(any())).thenReturn(enderecoResponseMock(enderecoMock));

            List<EnderecoResultadoDTO> resultados = service.adicionarEndereco(1L, List.of(dto));

            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).salvo()).isFalse();
            assertThat(resultados.get(0).motivo()).isEqualTo("Apenas um endereço pode ser principal");
        }

        @Test
        @DisplayName("Não deve adicionar endereço se pessoa não encontrada")
        void naoDeveAdicionarEnderecoSePessoaNaoEncontrada() {
            when(pessoaRepository.findByIdComEnderecos(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.adicionarEndereco(99L, List.of(enderecoSecundario())));
            verify(pessoaRepository, never()).save(any());
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

            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaMock));
            when(pessoaMapper.toDTO(pessoaMock)).thenReturn(responseMock);

            PessoaResponseDTO resultado = service.buscarPessoa(1L);

            assertThat(resultado.nome()).isEqualTo(pessoaMock.getNome());
            verify(pessoaRepository).findById(1L);
            verify(pessoaMapper).toDTO(pessoaMock);
        }

        @Test
        @DisplayName("Não deve buscar pessoa se não encontrada")
        void naoDeveBuscarPessoaNaoEncontrada() {
            when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.buscarPessoa(99L));
            verify(pessoaMapper, never()).toDTO(any());
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

            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaMock));
            when(pessoaRepository.save(pessoaMock)).thenReturn(pessoaMock);
            when(pessoaMapper.toDTO(pessoaMock)).thenReturn(responseMock);

            PessoaResponseDTO resultado = service.atualizarDados(1L, updateDTO);

            assertThat(resultado).isNotNull();
            assertThat(pessoaMock.getNome()).isEqualTo("Gabriela Atualizada");
            verify(pessoaRepository).save(pessoaMock);
        }

        @Test
        @DisplayName("Não deve atualizar se pessoa não encontrada")
        void naoDeveAtualizarSePessoaNaoEncontrada() {
            PessoaUpdateRequestDTO updateDTO = new PessoaUpdateRequestDTO("Gabriela Atualizada", null, null);

            when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> service.atualizarDados(99L, updateDTO));
            verify(pessoaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Não deve atualizar se endereço não encontrado")
        void naoDeveAtualizarSeEnderecoNaoEncontrado() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();
            EnderecoUpdateRequestDTO enderecoDTO = new EnderecoUpdateRequestDTO(999L, "Rua Nova", null, null, null, null, null, null);
            PessoaUpdateRequestDTO updateDTO = new PessoaUpdateRequestDTO(null, null, List.of(enderecoDTO));

            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaMock));

            assertThrows(EntityNotFoundException.class,
                    () -> service.atualizarDados(1L, updateDTO));
            verify(pessoaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deletarPessoa")
    class DeletarPessoa {

        @Test
        @DisplayName(" Deve deletar os dados da pessoa com sucesso")
        void deveDeletarPessoaComSucesso() {
            Pessoa pessoaMock = pessoaMockComUmPrincipal();

            when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoaMock));

            service.deletarPessoa(1L);

            verify(pessoaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Não deve deletar se os dados não forem encontrados")
        void naoDeveDeletarSeOsDadosNaoForemEncontrados() {
            when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> service.deletarPessoa(99L));

            verify(pessoaRepository, never()).deleteById(any());
        }
    }
}
