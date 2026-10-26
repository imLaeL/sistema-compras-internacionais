package com.projeto_pos.compras_internacionais.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_compras")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal valorUSD;

    @Column(nullable = false)
    private BigDecimal cotacaoUtilizada;

    @Column(nullable = false)
    private BigDecimal valorConvertido;

    @Column(nullable = false)
    private BigDecimal taxaServico;

    @Column(nullable = false)
    private BigDecimal tributoSimulado;

    @Column(nullable = false)
    private BigDecimal valorFinalBRL;

    @Column(nullable = false)
    private LocalDate dataCotacao;

    @Column(nullable = false)
    private LocalDateTime dataCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

}
