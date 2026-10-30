package com.projeto_pos.compras_internacionais.service;

import com.projeto_pos.compras_internacionais.dto.Cliente.LimiteResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraRequest;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.SimulacaoResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CompraService {
    SimulacaoResponse simular(BigDecimal valorUSD);
    CompraResponse registrar(CompraRequest request);
    List<CompraResponse> historico(UUID clienteId);
    LimiteResponse limite(UUID clienteId);
}
