package com.projeto_pos.compras_internacionais.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CotacaoVenda(
        BigDecimal valor,
        LocalDateTime data
) {}
