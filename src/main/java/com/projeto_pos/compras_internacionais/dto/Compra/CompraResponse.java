package com.projeto_pos.compras_internacionais.dto.Compra;

import com.projeto_pos.compras_internacionais.dto.Cliente.ClienteResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CompraResponse(
        UUID id,
        BigDecimal valorUSD,
        BigDecimal cotacaoUtilizada,
        BigDecimal valorConvertido,
        BigDecimal taxaServico,
        BigDecimal tributoSimulado,
        BigDecimal valorFinalBRL,
        LocalDate dataCotacao,
        LocalDateTime dataCompra,
        ClienteResponse cliente
) {}
