package com.projeto_pos.compras_internacionais.service.impl;

import com.projeto_pos.compras_internacionais.client.AwesomeApiClient;
import com.projeto_pos.compras_internacionais.dto.AwesomeApiResponse;
import com.projeto_pos.compras_internacionais.dto.CotacaoResponse;
import com.projeto_pos.compras_internacionais.dto.CotacaoVenda;
import com.projeto_pos.compras_internacionais.exception.CotacaoIndisponivelException;
import com.projeto_pos.compras_internacionais.exception.CotacaoInvalidaException;
import com.projeto_pos.compras_internacionais.exception.CotacaoTimeoutException;
import feign.FeignException;
import feign.RetryableException;
import feign.codec.DecodeException;
import com.projeto_pos.compras_internacionais.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CotacaoServiceImpl implements CotacaoService {

    private static final DateTimeFormatter FORMATO_CREATE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AwesomeApiClient awesomeApiClient;

    @Override
    public CotacaoVenda buscarCotacaoVenda() {
        AwesomeApiResponse resposta = consultarAwesomeApi();
        if (resposta == null || resposta.USDBRL() == null) {
            throw new CotacaoInvalidaException("resposta sem o objeto USDBRL");
        }

        CotacaoResponse usdbrl = resposta.USDBRL();
        return new CotacaoVenda(converterAsk(usdbrl.ask()), converterCreateDate(usdbrl.createDate()));
    }

    private AwesomeApiResponse consultarAwesomeApi() {
        try {
            return awesomeApiClient.consultarCotacaoDolar();
        } catch (RetryableException ex) {
            log.warn("Falha de comunicação com a AwesomeAPI: {}", ex.getMessage());
            if (ex.getCause() instanceof SocketTimeoutException) {
                throw new CotacaoTimeoutException();
            }
            throw new CotacaoIndisponivelException();
        } catch (DecodeException ex) {
            log.warn("Resposta ilegível da AwesomeAPI: {}", ex.getMessage());
            throw new CotacaoInvalidaException("resposta da AwesomeAPI em formato inesperado");
        } catch (FeignException ex) {
            log.warn("AwesomeAPI respondeu com status {}", ex.status());
            throw new CotacaoIndisponivelException();
        }
    }

    private BigDecimal converterAsk(String ask) {
        if (ask == null || ask.isBlank()) {
            throw new CotacaoInvalidaException("campo ask ausente");
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(ask.trim());
        } catch (NumberFormatException ex) {
            throw new CotacaoInvalidaException("campo ask não numérico: " + ask);
        }

        if (valor.signum() <= 0) {
            throw new CotacaoInvalidaException("cotação deve ser maior que zero");
        }
        return valor;
    }

    private LocalDateTime converterCreateDate(String createDate) {
        if (createDate == null || createDate.isBlank()) {
            throw new CotacaoInvalidaException("campo create_date ausente");
        }

        try {
            return LocalDateTime.parse(createDate.trim(), FORMATO_CREATE_DATE);
        } catch (DateTimeParseException ex) {
            throw new CotacaoInvalidaException("campo create_date em formato inválido: " + createDate);
        }
    }
}
