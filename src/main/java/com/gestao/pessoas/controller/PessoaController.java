package com.gestao.pessoas.controller;

import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import com.gestao.pessoas.dto.response.EnderecoResultadoDTO;
import com.gestao.pessoas.dto.response.PessoaResponseDTO;
import com.gestao.pessoas.service.PessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
@RequiredArgsConstructor
public class PessoaController {

    private final PessoaService service;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> cadastrarPessoa(@RequestBody @Valid PessoaRequestDTO dto) {
        PessoaResponseDTO response = service.cadastrarPessoa(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{idPessoa}/enderecos")
    public ResponseEntity<List<EnderecoResultadoDTO>> adicionarEnderecos(
            @PathVariable Long idPessoa,
            @RequestBody List<EnderecoRequestDTO> dtos) {
        List<EnderecoResultadoDTO> resultadoDTO = service.adicionarEndereco(idPessoa, dtos);
        return ResponseEntity
                .status(HttpStatus.MULTI_STATUS)
                .body(resultadoDTO);
    }

    @GetMapping
    public ResponseEntity<Page<PessoaResponseDTO>> listarPessoas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<PessoaResponseDTO> response = service.listarPessoas(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> buscarPessoa(
            @PathVariable Long id
    ) {
        PessoaResponseDTO response = service.buscarPessoa(id);
        return ResponseEntity
                .ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> atualizarPessoa(
            @PathVariable Long id,
            @RequestBody @Valid PessoaUpdateRequestDTO dto
    ) {
        PessoaResponseDTO response = service.atualizarDados(id, dto);
        return ResponseEntity
                .ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long id) {

        service.deletarPessoa(id);

        return ResponseEntity.noContent().build();
    }
}
