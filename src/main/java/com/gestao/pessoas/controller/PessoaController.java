package com.gestao.pessoas.controller;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.service.PessoaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarPessoa(@RequestBody @Valid PessoaRequestDTO dto) {
        service.cadastrarPessoa(dto);
    }

    @PostMapping("/{idPessoa}/enderecos")
    @ResponseStatus(HttpStatus.CREATED)
    public void adicionarEndereco(
            @PathVariable Long idPessoa,
            @RequestBody @Valid EnderecoRequestDTO dto) {
        service.adicionarEndereco(idPessoa, dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Parameters({
            @Parameter(name = "page", description = "Número da página", example = "0"),
            @Parameter(name = "size", description = "Itens por página", example = "10"),
            @Parameter(name = "sort", description = "Ordenação (ex: nome,asc)", example = "nome,asc")
    })
    public Page<PessoaResponseDTO> listarPessoas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        return service.listarPessoas(pageable);
    }
}
