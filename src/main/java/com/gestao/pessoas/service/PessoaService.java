package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.EnderecoUpdateRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import com.gestao.pessoas.dto.response.EnderecoResultadoDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.exception.CpfExisteException;
import com.gestao.pessoas.mapper.EnderecoMapper;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;
    private final EnderecoMapper enderecoMapper;

    @Transactional
    public PessoaResponseDTO cadastrarPessoa(PessoaRequestDTO dto) {
        if (pessoaRepository.existsByCpf(dto.cpf())) {
            throw new CpfExisteException();
        }

        Pessoa pessoa = pessoaMapper.toEntity(dto);

        if (pessoa.getEnderecos() != null) {
            pessoa.getEnderecos().forEach(endereco ->
                    endereco.setPessoa(pessoa));
            validarConsistenciaEnderecos(pessoa);
        }
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

        return pessoaMapper.toDTO(pessoaSalva);
    }

    @Transactional
    public List<EnderecoResultadoDTO> adicionarEndereco(Long idPessoa, List<EnderecoRequestDTO> dtos) {
        Pessoa pessoa = buscarPessoaComEnderecos(idPessoa);

        List<EnderecoResultadoDTO> resultados = new ArrayList<>();

        dtos.forEach(dto -> resultados.add(processarEndereco(pessoa, dto)));

        pessoaRepository.save(pessoa);

        return resultados;
    }

    public Page<PessoaResponseDTO> listarPessoas(Pageable pageable) {
        return pessoaRepository.findAll(pageable)
                .map(pessoaMapper::toDTO);
    }

    public PessoaResponseDTO buscarPessoa(Long id) {
        return pessoaMapper.toDTO(buscarPessoaPorId(id));
    }

    @Transactional
    public PessoaResponseDTO atualizarDados(Long id, PessoaUpdateRequestDTO dto) {
        Pessoa pessoa = buscarPessoaPorId(id);
        atualizarDadosPessoa(pessoa, dto);
        if (dto.enderecos() != null) {
            dto.enderecos().forEach(enderecoDTO ->
                    atualizarEnderecoPessoa(pessoa, enderecoDTO));
        }

        validarConsistenciaEnderecos(pessoa);

        return pessoaMapper.toDTO(pessoaRepository.save(pessoa));
    }

    @Transactional
    public void deletarPessoa(Long id) {
        buscarPessoaPorId(id);
        pessoaRepository.deleteById(id);
    }

    private Pessoa buscarPessoaPorId(Long id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
    }

    private void atualizarDadosPessoa(Pessoa pessoa, PessoaUpdateRequestDTO dto) {
        if (dto.nome() != null) pessoa.setNome(dto.nome());
        if (dto.dataNascimento() != null) pessoa.setDataNascimento(dto.dataNascimento());
    }

    private void atualizarEnderecoPessoa(Pessoa pessoa, EnderecoUpdateRequestDTO enderecoDTO) {
        Endereco endereco = buscarEnderecoDaPessoa(pessoa, enderecoDTO.idEndereco());
        if (enderecoDTO.rua() != null) endereco.setRua(enderecoDTO.rua());
        if (enderecoDTO.numero() != null) endereco.setNumero(enderecoDTO.numero());
        if (enderecoDTO.cidade() != null) endereco.setCidade(enderecoDTO.cidade());
        if (enderecoDTO.estado() != null) endereco.setEstado(enderecoDTO.estado());
        if (enderecoDTO.cep() != null) endereco.setCep(enderecoDTO.cep());
        if (enderecoDTO.isPrincipal() != null) {
            atualizarPrincipal(pessoa, endereco, enderecoDTO.isPrincipal());
        }
    }

    private Endereco buscarEnderecoDaPessoa(Pessoa pessoa, Long idEndereco) {
        return pessoa.getEnderecos().stream()
                .filter(e -> e.getIdEndereco().equals(idEndereco))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Endereço não encontrado: " + idEndereco));
    }

    private void atualizarPrincipal(Pessoa pessoa, Endereco endereco, Boolean isPrincipal) {
        if (Boolean.TRUE.equals(isPrincipal)) {
            pessoa.getEnderecos().forEach(e -> e.setIsPrincipal(false));
        }

        endereco.setIsPrincipal(isPrincipal);
    }

    private EnderecoResultadoDTO processarEndereco(Pessoa pessoa, EnderecoRequestDTO dto) {
        Endereco endereco = enderecoMapper.toEntity(dto);
        endereco.setPessoa(pessoa);

        try {
            pessoa.getEnderecos().add(endereco);
            validarConsistenciaEnderecos(pessoa);

            return new EnderecoResultadoDTO( enderecoMapper.toDTO(endereco), true, "Salvo com sucesso");

        } catch (IllegalArgumentException e) {
            pessoa.getEnderecos().remove(endereco);

            return new EnderecoResultadoDTO(enderecoMapper.toDTO(endereco), false, e.getMessage());
        }
    }

    private Pessoa buscarPessoaComEnderecos(Long idPessoa) {
        Pessoa pessoa = pessoaRepository.findByIdComEnderecos(idPessoa)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        if (pessoa.getEnderecos() == null) {
            pessoa.setEnderecos(new ArrayList<>());
        }

        return pessoa;
    }
    private void validarConsistenciaEnderecos(Pessoa pessoa) {
        validarApenaUmPrincipal(pessoa.getEnderecos());
        validarPeloMenosUmPrincipal(pessoa.getEnderecos());
    }

    private void validarApenaUmPrincipal(List<Endereco> enderecos) {
        long count = enderecos.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsPrincipal()))
                .count();
        if (count > 1) {
            throw new IllegalArgumentException("Apenas um endereço pode ser principal");
        }
    }

    private void validarPeloMenosUmPrincipal(List<Endereco> enderecos) {
        boolean temPrincipal = enderecos.stream()
                .anyMatch(e -> Boolean.TRUE.equals(e.getIsPrincipal()));
        if (!temPrincipal) {
            throw new IllegalArgumentException("É necessário ter ao menos um endereço principal");
        }
    }
}