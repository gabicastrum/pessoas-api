package com.gestao.pessoas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestao.pessoas.dto.request.EnderecoRequestDTO;
import com.gestao.pessoas.dto.request.PessoaRequestDTO;
import com.gestao.pessoas.dto.request.PessoaUpdateRequestDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cadastrarPessoa() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );

        PessoaRequestDTO request = new PessoaRequestDTO(
                "Gabriela",
                LocalDate.of(1999, 9, 9),
                "12345678909",
                List.of(endereco)
        );

        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Gabriela"))
                .andExpect(jsonPath("$.enderecos[0].cidade").value("Porto Alegre"));
    }

    @Test
    void adicionarEnderecos() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );

        PessoaRequestDTO request = new PessoaRequestDTO(
                "Gabriela",
                LocalDate.of(1999, 9, 9),
                "98765432100",
                List.of(endereco)
        );

        String response = mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long idPessoa = objectMapper.readTree(response).get("id").asLong();

        EnderecoRequestDTO novoEndereco = new EnderecoRequestDTO(
                "Av. Ipiranga", "456", "Centro", "Porto Alegre", "RS", "88888-888", false
        );
        mockMvc.perform(post("/pessoas/" + idPessoa + "/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(novoEndereco))))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$[0].endereco.rua").value("Av. Ipiranga"));
    }

    @Test
    void listarPessoas() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );
        mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PessoaRequestDTO("Edu", LocalDate.of(1980, 1, 1), "11144477735", List.of(endereco))
                        )))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/pessoas")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void buscarPessoa() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );
        PessoaRequestDTO request = new PessoaRequestDTO(
                "Maria", LocalDate.of(1990, 5, 5), "22233344405", List.of(endereco)
        );
        String response = mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idPessoa = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/pessoas/" + idPessoa))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void atualizarPessoa() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );
        PessoaRequestDTO request = new PessoaRequestDTO(
                "Carlos", LocalDate.of(1985, 3, 3), "33322211196", List.of(endereco)
        );
        String response = mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idPessoa = objectMapper.readTree(response).get("id").asLong();

        PessoaUpdateRequestDTO update = new PessoaUpdateRequestDTO("Carlos Laurindo", null, null);
        mockMvc.perform(patch("/pessoas/" + idPessoa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Laurindo"));
    }

    @Test
    void deletarPessoa() throws Exception {
        EnderecoRequestDTO endereco = new EnderecoRequestDTO(
                "Rua das Flores",
                "123",
                "Praia de Belas",
                "Porto Alegre",
                "RS",
                "99999-999",
                true
        );
        PessoaRequestDTO request = new PessoaRequestDTO(
                "Pedro", LocalDate.of(1970, 7, 7), "55566677788", List.of(endereco)
        );
        String response = mockMvc.perform(post("/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idPessoa = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/pessoas/" + idPessoa))
                .andExpect(status().isNoContent());
    }

}