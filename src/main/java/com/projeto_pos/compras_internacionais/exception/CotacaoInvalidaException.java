package com.projeto_pos.compras_internacionais.exception;

import org.springframework.http.HttpStatus;

public class CotacaoInvalidaException extends ApiException {

    public CotacaoInvalidaException(String motivo) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "COTACAO_INVALIDA", "Cotação inválida: " + motivo);
    }
}
