package com.gestao.pessoas.controller;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<PessoaResponseDTO> listarPessoas() {
        return service.listarPessoas();
    }
}
