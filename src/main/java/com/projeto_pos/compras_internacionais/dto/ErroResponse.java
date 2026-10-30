package com.projeto_pos.compras_internacionais.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String codigo,
        String mensagem,
        List<String> detalhes
) {}
