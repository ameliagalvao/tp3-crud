package com.example.tp3_crud;
import com.example.tp3_crud.domain.Funcionario;
import com.example.tp3_crud.repository.FuncionarioRepository;
import com.example.tp3_crud.web.FuncionarioController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FuncionarioController.class)
class FuncionarioControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean FuncionarioRepository repo;

    @Test
    void lista() throws Exception {
        var f = Funcionario.builder().id(1L).nome("João").email("joao@ex.com").cargo("Dev").salario(new BigDecimal("8000")).build();
        given(repo.findAll()).willReturn(List.of(f));

        mvc.perform(get("/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João"));
    }

    @Test
    void cria201SemBody() throws Exception {
        var in = Funcionario.builder().nome("Maria").email("maria@ex.com").cargo("Dev").salario(new BigDecimal("9000")).build();
        var out = Funcionario.builder().id(10L).nome("Maria").email("maria@ex.com").cargo("Dev").salario(new BigDecimal("9000")).build();
        given(repo.save(ArgumentMatchers.any(Funcionario.class))).willReturn(out);

        mvc.perform(post("/funcionarios").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/funcionarios/10")))
                .andExpect(content().string(""));
    }

    @Test
    void atualiza200() throws Exception {
        var existente = Funcionario.builder().id(2L).nome("Ana").email("ana@ex.com").cargo("Jr").salario(new BigDecimal("5000")).build();
        given(repo.findById(2L)).willReturn(Optional.of(existente));
        given(repo.save(ArgumentMatchers.any(Funcionario.class))).willAnswer(inv -> inv.getArgument(0));

        var upd = Funcionario.builder().nome("Ana Silva").email("ana.silva@ex.com").cargo("Sênior").salario(new BigDecimal("12000")).build();
        mvc.perform(put("/funcionarios/2").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Sênior"))
                .andExpect(jsonPath("$.salario").value(12000));
    }

    @Test
    void delete204() throws Exception {
        given(repo.existsById(77L)).willReturn(true);
        mvc.perform(delete("/funcionarios/77")).andExpect(status().isNoContent());
        verify(repo).deleteById(77L);
    }
}