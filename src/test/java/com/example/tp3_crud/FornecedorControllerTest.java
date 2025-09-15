package com.example.tp3_crud;

import com.example.tp3_crud.domain.Fornecedor;
import com.example.tp3_crud.repository.FornecedorRepository;
import com.example.tp3_crud.web.FornecedorController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FornecedorController.class)
class FornecedorControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean FornecedorRepository repo;

    @Test
    void cria201SemBody() throws Exception {
        var in = Fornecedor.builder().nome("ACME").cnpj("00.000.000/0001-00").email("acme@ex.com").build();
        var out = Fornecedor.builder().id(1L).nome("ACME").cnpj("00.000.000/0001-00").email("acme@ex.com").build();
        given(repo.save(ArgumentMatchers.any(Fornecedor.class))).willReturn(out);

        mvc.perform(post("/fornecedores").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/fornecedores/1")))
                .andExpect(content().string(""));
    }

    @Test
    void put200() throws Exception {
        var existente = Fornecedor.builder().id(9L).nome("Old").cnpj("11.111.111/1111-11").email("old@ex.com").build();
        given(repo.findById(9L)).willReturn(Optional.of(existente));
        given(repo.save(ArgumentMatchers.any(Fornecedor.class))).willAnswer(inv -> inv.getArgument(0));

        var upd = Fornecedor.builder().nome("Novo Nome").cnpj("11.111.111/1111-11").email("novo@ex.com").build();
        mvc.perform(put("/fornecedores/9").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.email").value("novo@ex.com"));
    }

    @Test
    void delete404() throws Exception {
        given(repo.existsById(123L)).willReturn(false);
        mvc.perform(delete("/fornecedores/123"))
                .andExpect(status().isNotFound());
    }
}