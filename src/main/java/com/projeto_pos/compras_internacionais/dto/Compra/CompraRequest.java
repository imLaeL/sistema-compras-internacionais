package com.projeto_pos.compras_internacionais.dto.Compra;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CompraRequest(

        @NotNull(message = "O ID do cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "O valor em dólares é obrigatório")
        @Positive(message = "O valor em dólares deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "O valor em dólares deve ter no máximo 2 casas decimais")
        BigDecimal valorUSD

) {}
