package com.projeto_pos.compras_internacionais.dto.Compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulacaoResponse(
        BigDecimal valorUSD,
        BigDecimal cotacaoUtilizada,
        LocalDateTime dataCotacao,
        BigDecimal valorConvertido,
        BigDecimal taxaServico,
        BigDecimal tributoSimulado,
        BigDecimal valorFinalBRL
) {}
