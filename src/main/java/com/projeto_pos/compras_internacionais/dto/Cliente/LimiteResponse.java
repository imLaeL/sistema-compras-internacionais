package com.projeto_pos.compras_internacionais.dto.Cliente;

import java.math.BigDecimal;

public record LimiteResponse(
        BigDecimal limiteMensal,
        BigDecimal totalUtilizado,
        BigDecimal saldoDisponivel
) {}
