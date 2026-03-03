package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
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
    public void cadastrarPessoa(PessoaRequestDTO dto) {
        if (pessoaRepository.existsByCpf(dto.cpf())) {
            throw new CpfExisteException();
        }

        Pessoa pessoa = pessoaMapper.toEntity(dto);

        if (pessoa.getEnderecos() != null) {
            validarApenaUmPrincipal(pessoa.getEnderecos());
            pessoa.getEnderecos().forEach(endereco->
                    endereco.setPessoa(pessoa));
        }
        pessoaRepository.save(pessoa);
    }

    @Transactional
    public void adicionarEndereco(Long idPessoa, EnderecoRequestDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(idPessoa).orElseThrow(
                () -> new EntityNotFoundException("Pessoa não encontrada"));

        if (pessoa.getEnderecos() == null) {
            pessoa.setEnderecos(new ArrayList<>());
        }

        if (Boolean.TRUE.equals(dto.isPrincipal())) {
            pessoa.getEnderecos().forEach(e -> e.setIsPrincipal(false));
        }

        Endereco endereco = enderecoMapper.toEntity(dto);
        endereco.setPessoa(pessoa);
        pessoa.getEnderecos().add(endereco);

        pessoaRepository.save(pessoa);
    }

    public Page<PessoaResponseDTO> listarPessoas(Pageable pageable) {
        return pessoaRepository.findAll(pageable)
                .map(pessoaMapper::toDTO);
    }

    public PessoaResponseDTO buscarPessoaPorId(Long id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        return pessoaMapper.toDTO(pessoa);
    }

    private void validarApenaUmPrincipal(List<Endereco> enderecos) {
        long count = enderecos.stream()
                .filter(e -> Boolean.TRUE.equals(e.getIsPrincipal()))
                .count();
        if (count > 1) {
            throw new IllegalArgumentException("Apenas um endereço pode ser principal");
        }
    }
}
