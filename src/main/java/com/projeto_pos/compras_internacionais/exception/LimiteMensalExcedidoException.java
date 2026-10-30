package com.projeto_pos.compras_internacionais.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class LimiteMensalExcedidoException extends ApiException {

    public LimiteMensalExcedidoException(BigDecimal saldoDisponivel, BigDecimal valorCompra) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "LIMITE_MENSAL_EXCEDIDO",
                "Compra de R$ " + valorCompra + " excede o saldo mensal disponível de R$ " + saldoDisponivel);
    }
}
