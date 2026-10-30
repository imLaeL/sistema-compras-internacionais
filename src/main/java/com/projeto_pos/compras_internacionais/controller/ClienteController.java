package com.projeto_pos.compras_internacionais.controller;

import com.projeto_pos.compras_internacionais.dto.Cliente.LimiteResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CompraService compraService;

    @GetMapping("/{id}/compras")
    public List<CompraResponse> historico(@PathVariable UUID id) {
        return compraService.historico(id);
    }

    @GetMapping("/{id}/limite")
    public LimiteResponse limite(@PathVariable UUID id) {
        return compraService.limite(id);
    }
}
