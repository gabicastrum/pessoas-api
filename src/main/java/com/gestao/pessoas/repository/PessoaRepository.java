package com.gestao.pessoas.repository;

import com.gestao.pessoas.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    //SELECT COUNT(*) FROM pessoas WHERE cpf = ?
    boolean existsByCpf(String cpf);
}
