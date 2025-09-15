package com.example.tp3_crud.repository;

import com.example.tp3_crud.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
