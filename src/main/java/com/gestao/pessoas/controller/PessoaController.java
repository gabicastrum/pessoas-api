package com.gestao.pessoas.controller;

import com.gestao.pessoas.domain.Pessoa;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.service.PessoaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<PessoaResponseDTO> listarPessoas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return service.listarPessoas(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PessoaResponseDTO buscarPessoa(
            @PathVariable Long id
    ) {
        return service.buscarPessoa(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public PessoaResponseDTO atualizarPessoa(
            @PathVariable Long id,
            @RequestBody @Valid PessoaUpdateRequestDTO dto
    ) {
        return service.atualizarDados(id, dto);
    }
}
