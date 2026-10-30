package com.projeto_pos.compras_internacionais.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClienteNaoEncontradoException extends ApiException {

    public ClienteNaoEncontradoException(UUID clienteId) {
        super(HttpStatus.NOT_FOUND, "CLIENTE_NAO_ENCONTRADO", "Cliente não encontrado: " + clienteId);
    }
}
