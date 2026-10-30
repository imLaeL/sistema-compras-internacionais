package com.projeto_pos.compras_internacionais.mappers;

import com.projeto_pos.compras_internacionais.dto.Cliente.ClienteRequest;
import com.projeto_pos.compras_internacionais.dto.Cliente.ClienteResponse;
import com.projeto_pos.compras_internacionais.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest dto) {
        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());

        return cliente;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail()
        );
    }
}
