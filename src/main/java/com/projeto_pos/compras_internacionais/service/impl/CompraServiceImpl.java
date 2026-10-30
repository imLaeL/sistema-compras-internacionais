package com.projeto_pos.compras_internacionais.service.impl;

import com.projeto_pos.compras_internacionais.dto.Cliente.LimiteResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraRequest;
import com.projeto_pos.compras_internacionais.dto.Compra.CompraResponse;
import com.projeto_pos.compras_internacionais.dto.Compra.SimulacaoResponse;
import com.projeto_pos.compras_internacionais.dto.CotacaoVenda;
import com.projeto_pos.compras_internacionais.exception.ClienteNaoEncontradoException;
import com.projeto_pos.compras_internacionais.exception.LimiteMensalExcedidoException;
import com.projeto_pos.compras_internacionais.mappers.CompraMapper;
import com.projeto_pos.compras_internacionais.model.Cliente;
import com.projeto_pos.compras_internacionais.model.Compra;
import com.projeto_pos.compras_internacionais.repository.ClienteRepository;
import com.projeto_pos.compras_internacionais.repository.CompraRepository;
import com.projeto_pos.compras_internacionais.service.CompraService;
import com.projeto_pos.compras_internacionais.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private static final BigDecimal LIMITE_MENSAL = new BigDecimal("10000.00");
    private static final BigDecimal VALOR_USD_TAXA_REDUZIDA = new BigDecimal("100");
    private static final BigDecimal TAXA_SERVICO_PADRAO = new BigDecimal("0.05");
    private static final BigDecimal TAXA_SERVICO_REDUZIDA = new BigDecimal("0.02");
    private static final BigDecimal PERCENTUAL_TRIBUTO = new BigDecimal("0.10");

    private final CotacaoService cotacaoService;
    private final ClienteRepository clienteRepository;
    private final CompraRepository compraRepository;
    private final CompraMapper compraMapper;

    @Override
    public SimulacaoResponse simular(BigDecimal valorUSD) {
        return calcular(valorUSD, cotacaoService.buscarCotacaoVenda());
    }

    // ponytail: no lock on the client, two concurrent purchases can both pass the limit; use a PESSIMISTIC_WRITE lookup if that matters
    @Override
    @Transactional
    public CompraResponse registrar(CompraRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(request.clienteId()));

        SimulacaoResponse calculo = calcular(request.valorUSD(), cotacaoService.buscarCotacaoVenda());

        LocalDateTime agora = LocalDateTime.now();
        BigDecimal saldo = saldoDisponivel(totalDoMes(cliente.getId(), agora));
        if (calculo.valorFinalBRL().compareTo(saldo) > 0) {
            throw new LimiteMensalExcedidoException(saldo, calculo.valorFinalBRL());
        }

        Compra compra = compraRepository.save(compraMapper.toEntity(calculo, cliente, agora));
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> historico(UUID clienteId) {
        garantirClienteExiste(clienteId);
        return compraRepository.findByClienteIdOrderByDataCompraDesc(clienteId).stream()
                .map(compraMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LimiteResponse limite(UUID clienteId) {
        garantirClienteExiste(clienteId);
        BigDecimal totalUtilizado = totalDoMes(clienteId, LocalDateTime.now());
        return new LimiteResponse(LIMITE_MENSAL, totalUtilizado, saldoDisponivel(totalUtilizado));
    }

    private SimulacaoResponse calcular(BigDecimal valorUSD, CotacaoVenda cotacao) {
        BigDecimal valorConvertido = arredondar(valorUSD.multiply(cotacao.valor()));
        BigDecimal taxaServico = arredondar(valorConvertido.multiply(percentualTaxaServico(valorUSD)));
        BigDecimal tributoSimulado = arredondar(valorConvertido.multiply(PERCENTUAL_TRIBUTO));
        BigDecimal valorFinalBRL = valorConvertido.add(taxaServico).add(tributoSimulado);

        return new SimulacaoResponse(valorUSD, cotacao.valor(), cotacao.data(),
                valorConvertido, taxaServico, tributoSimulado, valorFinalBRL);
    }

    private BigDecimal percentualTaxaServico(BigDecimal valorUSD) {
        if (valorUSD.compareTo(VALOR_USD_TAXA_REDUZIDA) < 0) {
            return TAXA_SERVICO_PADRAO;
        }
        return TAXA_SERVICO_REDUZIDA;
    }

    private BigDecimal saldoDisponivel(BigDecimal totalUtilizado) {
        return LIMITE_MENSAL.subtract(totalUtilizado).max(BigDecimal.ZERO);
    }

    private BigDecimal totalDoMes(UUID clienteId, LocalDateTime referencia) {
        LocalDateTime inicioDoMes = referencia.toLocalDate().withDayOfMonth(1).atStartOfDay();
        return compraRepository.somarComprasDoClienteNoPeriodo(clienteId, inicioDoMes, inicioDoMes.plusMonths(1));
    }

    private void garantirClienteExiste(UUID clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNaoEncontradoException(clienteId);
        }
    }

    private BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
