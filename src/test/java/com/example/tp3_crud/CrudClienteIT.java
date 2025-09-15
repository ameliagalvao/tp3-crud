package com.example.tp3_crud;

import com.example.tp3_crud.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CrudClienteIT {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    String url(String p) { return "http://localhost:" + port + p; }

    @Test
    void fluxoCRUD() {
        // POST (201, sem body)
        var novo = new Cliente(null, "Alice", "11122233344", "alice@example.com");
        ResponseEntity<Void> created = rest.postForEntity(url("/clientes"), novo, Void.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var location = created.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // GET por Location (200 + body)
        Cliente got = rest.getForObject(location, Cliente.class);
        assertThat(got.getNome()).isEqualTo("Alice");

        // PUT (200)
        got.setNome("Alice Silva");
        rest.put(location, got);
        Cliente after = rest.getForObject(location, Cliente.class);
        assertThat(after.getNome()).isEqualTo("Alice Silva");

        // DELETE (204)
        rest.delete(location);
        ResponseEntity<String> notFound = rest.getForEntity(location, String.class);
        assertThat(notFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
