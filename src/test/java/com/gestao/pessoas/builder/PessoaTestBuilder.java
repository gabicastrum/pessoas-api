package com.gestao.pessoas.builder;

import com.gestao.pessoas.domain.Endereco;
import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PessoaTestBuilder {

    //EnderecoRequestDTO

    public static EnderecoRequestDTO enderecoPrincipal() {
        return new EnderecoRequestDTO(
                "Rua das Flores", "100", "Centro",
                "São Paulo", "SP", "01310-100", true);
    }

    public static EnderecoRequestDTO enderecoSecundario() {
        return new EnderecoRequestDTO(
                "Rua Augusta", "500", "Consolação",
                "São Paulo", "SP", "01305-000", false);
    }

    public static EnderecoRequestDTO enderecoSemPrincipal() {
        return new EnderecoRequestDTO(
                "Rua Oscar Freire", "200", "Jardins",
                "São Paulo", "SP", "01426-001", null);
    }

    //PessoaRequestDTO

    public static PessoaRequestDTO pessoaComUmEndereco() {
        return new PessoaRequestDTO(
                "Gabriela",
                LocalDate.of(2010, 10, 21),
                "111.111.111-00",
                List.of(enderecoPrincipal()));
    }

    public static PessoaRequestDTO pessoaComTresEnderecos() {
        return new PessoaRequestDTO(
                "Maria Oliveira",
                LocalDate.of(1985, 3, 20),
                "987.654.321-00",
                List.of(enderecoPrincipal(), enderecoSecundario(), enderecoSemPrincipal()));
    }

    public static PessoaRequestDTO pessoaSemEnderecos() {
        return new PessoaRequestDTO(
                "Ana Lima",
                LocalDate.of(1995, 7, 30),
                "444.555.666-00",
                null);
    }

    public static PessoaRequestDTO pessoaComDoisPrincipais() {
        EnderecoRequestDTO segundoPrincipal = new EnderecoRequestDTO(
                "Av. Paulista", "1000", "Bela Vista",
                "São Paulo", "SP", "01310-200", true);

        return new PessoaRequestDTO(
                "Carlos Souza",
                LocalDate.of(2000, 1, 10),
                "111.222.333-00",
                List.of(enderecoPrincipal(), segundoPrincipal));
    }

    //PessoaResponseDTO
    public static PessoaResponseDTO pessoaResponseMock(Pessoa pessoa) {
        return new PessoaResponseDTO(
                1L,
                pessoa.getNome(),
                pessoa.getCpf(),
                List.of()
        );
    }

    //Pessoa (entidade mock)

    public static Pessoa pessoaMock(PessoaRequestDTO dto) {
        Endereco principal = Endereco.builder().isPrincipal(true).build();
        return Pessoa.builder()
                .nome(dto.nome())
                .enderecos(new ArrayList<>(List.of(principal)))
                .build();
    }

    public static Pessoa pessoaMockComDoisPrincipais(PessoaRequestDTO dto) {
        Endereco e1 = Endereco.builder().isPrincipal(true).build();
        Endereco e2 = Endereco.builder().isPrincipal(true).build();

        return Pessoa.builder()
                .nome(dto.nome())
                .enderecos(new ArrayList<>(List.of(e1, e2)))
                .build();
    }
}