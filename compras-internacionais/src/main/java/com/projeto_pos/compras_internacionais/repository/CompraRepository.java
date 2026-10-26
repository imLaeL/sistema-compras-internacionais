package com.projeto_pos.compras_internacionais.repository;

import com.projeto_pos.compras_internacionais.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface CompraRepository extends JpaRepository<Compra, UUID> {

    @Query("""
        SELECT COALESCE(SUM(c.valorFinalBRL), 0)
        FROM Compra c
        WHERE c.cliente.id = :clienteId
          AND c.dataCompra >= :inicio
          AND c.dataCompra < :fim
        """)
    BigDecimal somarComprasDoClienteNoPeriodo(
            @Param("clienteId") UUID clienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}
