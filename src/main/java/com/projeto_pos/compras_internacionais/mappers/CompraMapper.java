package com.projeto_pos.compras_internacionais.mappers;

import com.projeto_pos.compras_internacionais.dto.Compra.CompraRequest;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.model.Cliente;
import com.projeto_pos.compras_internacionais.model.Compra;
import org.springframework.stereotype.Component;

@Component
public class CompraMapper {

    private final ClienteMapper clienteMapper;

    public CompraMapper(ClienteMapper clienteMapper) {
        this.clienteMapper = clienteMapper;
    }

    public Compra toEntity(
            CompraRequest dto,
            Cliente cliente
    ) {
        Compra compra = new Compra();

        compra.setValorUSD(dto.valorUSD());
        compra.setCliente(cliente);

        return compra;
    }

    public CompraResponse toResponse(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getValorUSD(),
                compra.getCotacaoUtilizada(),
                compra.getValorConvertido(),
                compra.getTaxaServico(),
                compra.getTributoSimulado(),
                compra.getValorFinalBRL(),
                compra.getDataCotacao(),
                compra.getDataCompra(),
                clienteMapper.toResponse(compra.getCliente())
        );
    }
}
