package com.gestao.pessoas.controller;

import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
