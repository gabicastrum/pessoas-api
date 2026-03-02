package com.gestao.pessoas.service;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.exception.CpfExisteException;
import com.gestao.pessoas.mapper.PessoaMapper;
import com.gestao.pessoas.repository.PessoaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //cria o construtor para injeção de automatica
public class PessoaService {
    //pq private final?
    //pq o repository e o mapper
    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    //pq o uso de void?
    // pq RuntimeException e não outro?
    @Transactional
    public void cadastrarPessoa(PessoaRequestDTO dto) {
        if (pessoaRepository.existsByCpf(dto.cpf())) {
            throw new CpfExisteException();
        }

        //convertendo DTO para a entidade
        Pessoa pessoa = pessoaMapper.toEntity(dto);

        //Conectando
        if (pessoa.getEnderecos() != null) {
            //para cada endereco da lista...
            pessoa.getEnderecos().forEach(endereco->
                    //dizemos: ei, sua pessoa está aqui endereco
                    endereco.setPessoa(pessoa));
        }
        //e então salvamos
        pessoaRepository.save(pessoa);
    }

}
