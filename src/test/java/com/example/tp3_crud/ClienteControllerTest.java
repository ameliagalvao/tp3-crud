package com.example.tp3_crud;

import com.example.tp3_crud.domain.Cliente;
import com.example.tp3_crud.repository.ClienteRepository;
import com.example.tp3_crud.web.ClienteController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteController.class)
public class ClienteControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ClienteRepository repo;

    @Test
    void deveListarClientes() throws Exception {
        var c1 = new Cliente(1L, "Alice", "11122233344", "alice@ex.com");
        var c2 = new Cliente(2L, "Bob",   "22233344455", "bob@ex.com");
        given(repo.findAll()).willReturn(List.of(c1, c2));

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Alice"));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        var c = new Cliente(10L, "Carol", "33344455566", "carol@ex.com");
        given(repo.findById(10L)).willReturn(Optional.of(c));

        mvc.perform(get("/clientes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Carol"));
    }

    @Test
    void deveRetornar404QuandoNaoExiste() throws Exception {
        given(repo.findById(99L)).willReturn(Optional.empty());

        mvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriarCliente_Com201ELocationSemBody() throws Exception {
        var body = new Cliente(null, "Dan", "44455566677", "dan@ex.com");
        var salvo = new Cliente(5L, "Dan", "44455566677", "dan@ex.com");
        given(repo.save(ArgumentMatchers.any(Cliente.class))).willReturn(salvo);

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/clientes/5")))
                .andExpect(content().string("")); // sem body
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        var existente = new Cliente(7L, "Eva", "55566677788", "eva@ex.com");
        given(repo.findById(7L)).willReturn(Optional.of(existente));
        // repo.save devolve a entidade atualizada
        given(repo.save(ArgumentMatchers.any(Cliente.class)))
                .willAnswer(inv -> inv.getArgument(0));

        var atualizado = new Cliente(null, "Eva Lima", "55566677788", "eva.lima@ex.com");
        mvc.perform(put("/clientes/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Eva Lima"))
                .andExpect(jsonPath("$.email").value("eva.lima@ex.com"));
    }

    @Test
    void deveDeletarCliente_204() throws Exception {
        given(repo.existsById(3L)).willReturn(true);

        mvc.perform(delete("/clientes/3"))
                .andExpect(status().isNoContent());

        verify(repo).deleteById(3L);
    }

    @Test
    void deleteDeveRetornar404QuandoNaoExiste() throws Exception {
        given(repo.existsById(123L)).willReturn(false);

        mvc.perform(delete("/clientes/123"))
                .andExpect(status().isNotFound());
    }
}
