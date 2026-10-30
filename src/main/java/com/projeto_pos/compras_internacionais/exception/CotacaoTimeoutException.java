package com.projeto_pos.compras_internacionais.exception;

import org.springframework.http.HttpStatus;

public class CotacaoTimeoutException extends ApiException {

    public CotacaoTimeoutException() {
        super(HttpStatus.GATEWAY_TIMEOUT, "COTACAO_TIMEOUT",
                "Serviço de cotação não respondeu a tempo. Tente novamente mais tarde");
    }
}
