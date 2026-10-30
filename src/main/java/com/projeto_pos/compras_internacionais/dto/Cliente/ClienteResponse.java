package com.projeto_pos.compras_internacionais.dto.Cliente;

import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String email
) {}
