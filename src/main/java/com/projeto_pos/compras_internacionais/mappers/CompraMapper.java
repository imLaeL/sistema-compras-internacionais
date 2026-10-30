package com.projeto_pos.compras_internacionais.mappers;

import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.SimulacaoResponse;
import com.projeto_pos.compras_internacionais.model.Cliente;
import com.projeto_pos.compras_internacionais.model.Compra;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompraMapper {

    private final ClienteMapper clienteMapper;

    public CompraMapper(ClienteMapper clienteMapper) {
        this.clienteMapper = clienteMapper;
    }

    public Compra toEntity(
            SimulacaoResponse calculo,
            Cliente cliente,
            LocalDateTime dataCompra
    ) {
        Compra compra = new Compra();

        compra.setValorUSD(calculo.valorUSD());
        compra.setCotacaoUtilizada(calculo.cotacaoUtilizada());
        compra.setDataCotacao(calculo.dataCotacao());
        compra.setValorConvertido(calculo.valorConvertido());
        compra.setTaxaServico(calculo.taxaServico());
        compra.setTributoSimulado(calculo.tributoSimulado());
        compra.setValorFinalBRL(calculo.valorFinalBRL());
        compra.setDataCompra(dataCompra);
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
