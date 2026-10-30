package com.projeto_pos.compras_internacionais.client;

import com.projeto_pos.compras_internacionais.dto.AwesomeApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "awesomeApiClient",
        url = "${integrations.awesomeapi.url}"
)
public interface AwesomeApiClient {

    @GetMapping("/json/last/USD-BRL")
    AwesomeApiResponse consultarCotacaoDolar();
}