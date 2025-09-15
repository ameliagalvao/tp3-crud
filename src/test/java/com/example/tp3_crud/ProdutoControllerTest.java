package com.example.tp3_crud;


import com.example.tp3_crud.domain.Produto;
import com.example.tp3_crud.repository.ProdutoRepository;
import com.example.tp3_crud.web.ProdutoController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProdutoController.class)
class ProdutoControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean ProdutoRepository repo;

    @Test
    void cria201SemBody() throws Exception {
        var in = Produto.builder().nome("Mouse").sku("SKU-XYZ").preco(new BigDecimal("120.00")).build();
        var out = Produto.builder().id(4L).nome("Mouse").sku("SKU-XYZ").preco(new BigDecimal("120.00")).build();
        given(repo.save(ArgumentMatchers.any(Produto.class))).willReturn(out);

        mvc.perform(post("/produtos").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/produtos/4")))
                .andExpect(content().string(""));
    }

    @Test
    void put200() throws Exception {
        var existente = Produto.builder().id(8L).nome("Caneca").sku("SKU-001").preco(new BigDecimal("25.90")).build();
        given(repo.findById(8L)).willReturn(Optional.of(existente));
        given(repo.save(ArgumentMatchers.any(Produto.class))).willAnswer(inv -> inv.getArgument(0));

        var upd = Produto.builder().nome("Caneca Premium").sku("SKU-001").preco(new BigDecimal("29.90")).build();
        mvc.perform(put("/produtos/8").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Caneca Premium"))
                .andExpect(jsonPath("$.preco").value(29.90));
    }

    @Test
    void delete204() throws Exception {
        given(repo.existsById(2L)).willReturn(true);
        mvc.perform(delete("/produtos/2")).andExpect(status().isNoContent());
        verify(repo).deleteById(2L);
    }
}