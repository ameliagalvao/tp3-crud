package com.example.tp3_crud.web;

import com.example.tp3_crud.domain.Funcionario;
import com.example.tp3_crud.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {
    private final FuncionarioRepository repo;


    @RequestMapping(method = RequestMethod.GET)
    public List<Funcionario> list() { return repo.findAll(); }


    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public Funcionario get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario " + id + " não encontrado"));
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Funcionario> create(@RequestBody Funcionario body) {
        if (body.getId() != null) {
            throw new IllegalArgumentException("Id deve ser nulo no POST");
        }
        Funcionario saved = repo.save(body);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    public Funcionario update(@PathVariable Long id, @RequestBody Funcionario body) {
        Funcionario f = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario " + id + " não encontrado"));
        f.setNome(body.getNome());
        f.setEmail(body.getEmail());
        f.setCargo(body.getCargo());
        f.setSalario(body.getSalario());
        return repo.save(f);
    }


    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario " + id + " não encontrado");
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
