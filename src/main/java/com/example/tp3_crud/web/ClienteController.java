package com.example.tp3_crud.web;

import com.example.tp3_crud.domain.Cliente;
import com.example.tp3_crud.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository repo;


    @RequestMapping(method = RequestMethod.GET)
    public List<Cliente> list() { return repo.findAll(); }


    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public Cliente get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente " + id + " não encontrado"));
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> create(@RequestBody Cliente body) {
        if (body.getId() != null) throw new IllegalArgumentException("Id deve ser nulo no POST");
        Cliente saved = repo.save(body);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()   // /clientes
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public Cliente update(@PathVariable Long id, @RequestBody Cliente body) {
        Cliente e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente " + id + " não encontrado"));
        e.setNome(body.getNome());
        e.setCpf(body.getCpf());
        e.setEmail(body.getEmail());
        return repo.save(e);
    }


    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente " + id + " não encontrado");
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}