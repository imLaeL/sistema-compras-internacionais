package com.projeto_pos.compras_internacionais.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CotacaoResponse(
        String code,
        String codein,
        String name,
        String bid,

        @JsonProperty("ask")
        String ask,

        String high,
        String low,
        String timestamp,

        @JsonProperty("create_date")
        String createDate
) {}
