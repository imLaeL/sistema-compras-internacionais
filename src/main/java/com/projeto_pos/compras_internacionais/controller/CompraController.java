package com.projeto_pos.compras_internacionais.controller;

import com.projeto_pos.compras_internacionais.dto.Compra.CompraRequest;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.SimulacaoRequest;
import com.projeto_pos.compras_internacionais.dto.Compra.SimulacaoResponse;
import com.projeto_pos.compras_internacionais.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping("/simular")
    public SimulacaoResponse simular(@Valid @RequestBody SimulacaoRequest request) {
        return compraService.simular(request.valorUSD());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse registrar(@Valid @RequestBody CompraRequest request) {
        return compraService.registrar(request);
    }
}
