package com.projeto_pos.compras_internacionais.exception;

import org.springframework.http.HttpStatus;

public class CotacaoIndisponivelException extends ApiException {

    public CotacaoIndisponivelException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, "COTACAO_INDISPONIVEL",
                "Serviço de cotação indisponível. Tente novamente mais tarde");
    }
}
