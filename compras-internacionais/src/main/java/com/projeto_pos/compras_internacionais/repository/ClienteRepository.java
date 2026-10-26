package com.projeto_pos.compras_internacionais.repository;


import com.projeto_pos.compras_internacionais.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository
        extends JpaRepository<Cliente, UUID> {

    boolean existsByEmail(String email);
}