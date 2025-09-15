package com.example.tp3_crud.web;

import com.example.tp3_crud.domain.Fornecedor;
import com.example.tp3_crud.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {
    private final FornecedorRepository repo;


    @RequestMapping(method = RequestMethod.GET)
    public List<Fornecedor> list() { return repo.findAll(); }


    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public Fornecedor get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor " + id + " não encontrado"));
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Fornecedor> create(@RequestBody Fornecedor body) {
        if (body.getId() != null) {
            throw new IllegalArgumentException("Id deve ser nulo no POST");
        }
        Fornecedor saved = repo.save(body);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public Fornecedor update(@PathVariable Long id, @RequestBody Fornecedor body) {
        Fornecedor e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor " + id + " não encontrado"));
        e.setNome(body.getNome());
        e.setCnpj(body.getCnpj());
        e.setEmail(body.getEmail());
        return repo.save(e);
    }


    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor " + id + " não encontrado");
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}