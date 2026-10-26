CREATE TABLE tb_clientes (
     id BINARY(16) NOT NULL,
     nome VARCHAR(255) NOT NULL,
     email VARCHAR(255) NOT NULL,

     CONSTRAINT pk_clientes PRIMARY KEY (id),
     CONSTRAINT uk_clientes_email UNIQUE (email)
);

CREATE TABLE tb_compras (
    id BINARY(16) NOT NULL,
    valor_usd DECIMAL(19, 2) NOT NULL,
    cotacao_utilizada DECIMAL(19, 6) NOT NULL,
    valor_convertido DECIMAL(19, 2) NOT NULL,
    taxa_servico DECIMAL(19, 2) NOT NULL,
    tributo_simulado DECIMAL(19, 2) NOT NULL,
    valor_final_brl DECIMAL(19, 2) NOT NULL,
    data_cotacao DATE NOT NULL,
    data_compra DATETIME(6) NOT NULL,
    cliente_id BINARY(16) NOT NULL,

    CONSTRAINT pk_compras PRIMARY KEY (id),

    CONSTRAINT fk_compras_clientes
        FOREIGN KEY (cliente_id)
            REFERENCES tb_clientes(id)
            ON DELETE RESTRICT
);

CREATE INDEX idx_compras_cliente_id
    ON tb_compras(cliente_id);
