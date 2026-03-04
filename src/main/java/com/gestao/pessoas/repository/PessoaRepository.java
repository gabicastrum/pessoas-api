package com.gestao.pessoas.repository;

import com.gestao.pessoas.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    //SELECT COUNT(*) FROM pessoas WHERE cpf = ?
    boolean existsByCpf(String cpf);

    @Query("SELECT p FROM Pessoa p JOIN FETCH p.enderecos WHERE p.id = :id")
    Optional<Pessoa> findByIdComEnderecos(@Param("id") Long id);
}
