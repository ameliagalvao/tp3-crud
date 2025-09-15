package com.example.tp3_crud.repository;

import com.example.tp3_crud.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
