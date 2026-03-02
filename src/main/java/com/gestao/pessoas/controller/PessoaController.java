package com.gestao.pessoas.controller;

import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarPessoa(@RequestBody PessoaRequestDTO dto) {
        service.cadastrarPessoa(dto);
    }
}
