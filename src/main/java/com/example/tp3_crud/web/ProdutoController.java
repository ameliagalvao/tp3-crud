package com.example.tp3_crud.web;

import com.example.tp3_crud.domain.Produto;
import com.example.tp3_crud.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository repo;


    @RequestMapping(method = RequestMethod.GET)
    public List<Produto> list() { return repo.findAll(); }


    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public Produto get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto " + id + " não encontrado"));
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Produto> create(@RequestBody Produto body) {
        if (body.getId() != null) {
            throw new IllegalArgumentException("Id deve ser nulo no POST");
        }
        Produto saved = repo.save(body);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public Produto update(@PathVariable Long id, @RequestBody Produto body) {
        Produto e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto " + id + " não encontrado"));
        e.setNome(body.getNome());
        e.setSku(body.getSku());
        e.setPreco(body.getPreco());
        return repo.save(e);
    }


    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto " + id + " não encontrado");
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}