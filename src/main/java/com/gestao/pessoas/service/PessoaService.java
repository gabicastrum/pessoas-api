package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.exception.CpfExisteException;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    @Transactional
    public void cadastrarPessoa(PessoaRequestDTO dto) {
        if (pessoaRepository.existsByCpf(dto.cpf())) {
            throw new CpfExisteException();
        }

        Pessoa pessoa = pessoaMapper.toEntity(dto);

        if (pessoa.getEnderecos() != null) {
            pessoa.getEnderecos().forEach(endereco->
                    endereco.setPessoa(pessoa));
        }
        pessoaRepository.save(pessoa);
    }

}
