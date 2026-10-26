package com.projeto_pos.compras_internacionais.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwesomeApi(

        @JsonProperty("USDBRL")
        CotacaoResponse USDBRL

) {}

