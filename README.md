# sistema-compras-internacionais — API REST de Compras Internacionais

Exercício da disciplina **Programação Web Back-end II** (Pós-graduação Full Stack, 2026.2): API REST que registra compras de clientes em dólar (USD), consulta a cotação de venda na [AwesomeAPI](https://docs.awesomeapi.com.br/api-de-moedas), converte para reais (BRL) e aplica taxa de serviço, tributo didático e limite mensal.

**Participantes:** Antonio Dias e Isaque Lael

## Stack

- Java 25, Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- Spring Cloud OpenFeign (cliente da AwesomeAPI)
- MySQL 8.4 + Flyway (migrations em `src/main/resources/db/migration`)
- Lombok

## Arquitetura

```
Cliente HTTP → CompraController / ClienteController → CompraService (interface) → CompraServiceImpl → CompraRepository / ClienteRepository → MySQL
                                                                                         ↘ CotacaoService (interface) → CotacaoServiceImpl → AwesomeApiClient (Feign) → AwesomeAPI
```

- As entidades `Cliente` e `Compra` nunca são expostas: a API recebe e devolve DTOs (`dto/`), convertidos pelos mappers (`mappers/`).
- Erros são lançados como exceções que estendem `ApiException` e tratados em um único `@RestControllerAdvice` (`GlobalExceptionHandler`).

## Como executar

Pré-requisitos: **Java 25** e **Docker**.

```bash
# 1. Subir o MySQL (porta 3306, banco compras-internacionais-db, usuário root/root)
docker compose up -d

# 2. Subir a aplicação (o Flyway cria as tabelas e os clientes na primeira execução)
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

- **Roteiro de testes:** [`api.http`](api.http). Rode em ordem no HTTP Client do IntelliJ; cada requisição tem asserts que conferem as regras de negócio.
- Para parar o banco: `docker compose down` (ou `docker compose down -v` para apagar os dados).

> **IntelliJ:** se a importação do Maven falhar com "nenhum projeto Maven", em *Settings → Build Tools → Maven* troque *Maven home path* para **Bundled (Maven 3)**. O importador do IntelliJ não carrega o Maven 3.10 do wrapper.

## Configuração do banco de dados

O banco de desenvolvimento é o MySQL do [`compose.yaml`](compose.yaml). A conexão está em `src/main/resources/application.yaml` e pode ser sobrescrita por variáveis de ambiente:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/compras-internacionais-db?createDatabaseIfNotExist=true` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | `root` |

O schema é criado só pelo Flyway; o Hibernate roda com `ddl-auto: validate` e impede a aplicação de subir se a entidade divergir da tabela.

| Migration | Conteúdo |
|---|---|
| `V1__create_table_clientes_compras.sql` | tabelas `tb_clientes` e `tb_compras` (FK `cliente_id`) |
| `V2__seed_clientes.sql` | clientes de teste (não há endpoint de cadastro de clientes) |
| `V3__alter_data_cotacao_to_datetime.sql` | `data_cotacao` passa a guardar data e hora |

### Clientes cadastrados

| Nome | id |
|---|---|
| Ana Souza | `11111111-1111-1111-1111-111111111111` |
| Bruno Lima | `22222222-2222-2222-2222-222222222222` |
| Carla Mendes | `33333333-3333-3333-3333-333333333333` |

## Configuração da integração externa

A cotação vem de `GET https://economia.awesomeapi.com.br/json/last/USD-BRL` pelo `AwesomeApiClient` (`@FeignClient`). Não é preciso chave de API.

```yaml
integrations:
  awesomeapi:
    url: https://economia.awesomeapi.com.br   # sobrescreva com INTEGRATIONS_AWESOMEAPI_URL

spring:
  cloud:
    openfeign:
      client:
        config:
          awesomeApiClient:
            connect-timeout: 3000   # ms
            read-timeout: 5000      # ms
```

- Usa o campo `ask` (cotação de venda), recebido como texto e convertido para `BigDecimal`. O `bid` nunca é usado.
- O `create_date` é gravado na compra como `dataCotacao`.
- A cotação nunca vem do cliente: um campo de cotação enviado no corpo é ignorado.

| Situação na AwesomeAPI | Resposta da nossa API |
|---|---|
| `USDBRL`, `ask` ou `create_date` ausentes/inválidos, ou `ask` ≤ 0 | 422 `COTACAO_INVALIDA` |
| Fora do ar ou respondendo com erro | 503 `COTACAO_INDISPONIVEL` |
| Sem resposta dentro do `read-timeout` | 504 `COTACAO_TIMEOUT` |

Para simular a API fora do ar: `INTEGRATIONS_AWESOMEAPI_URL=http://localhost:9999 ./mvnw spring-boot:run`.

## Endpoints

| Método | Caminho | Sucesso | Erros |
|---|---|---|---|
| POST | `/compras/simular` | 200 OK (não grava) | 400, 422, 503, 504 |
| POST | `/compras` | 201 Created | 400, 404, 422, 503, 504 |
| GET | `/clientes/{id}/compras` | 200 OK | 400, 404 |
| GET | `/clientes/{id}/limite` | 200 OK | 400, 404 |

### Exemplo

```http
POST /compras
Content-Type: application/json

{
  "clienteId": "11111111-1111-1111-1111-111111111111",
  "valorUSD": 200.00
}
```

```json
{
  "id": "50dd496f-...",
  "valorUSD": 200.00,
  "cotacaoUtilizada": 5.4000,
  "valorConvertido": 1080.00,
  "taxaServico": 21.60,
  "tributoSimulado": 108.00,
  "valorFinalBRL": 1209.60,
  "dataCotacao": "2026-10-16T17:00:00",
  "dataCompra": "2026-10-16T17:05:12.345678",
  "cliente": {
    "id": "11111111-1111-1111-1111-111111111111",
    "nome": "Ana Souza",
    "email": "ana.souza@email.com"
  }
}
```

`POST /compras/simular` recebe só `{ "valorUSD": 200.00 }` e devolve os mesmos valores calculados, sem `id`, `dataCompra` e `cliente`.

`GET /clientes/{id}/limite` devolve o mês-calendário atual:

```json
{ "limiteMensal": 10000.00, "totalUtilizado": 1209.60, "saldoDisponivel": 8790.40 }
```

## Regras de negócio

Valores em `BigDecimal`, arredondados para 2 casas com `HALF_UP` em cada etapa.

1. **RN01 — Cotação:** sempre o `ask` da AwesomeAPI.
2. **RN02 — Conversão:** `valorConvertido = valorUSD × cotacaoUtilizada`.
3. **RN03 — Taxa de serviço:** 5% do `valorConvertido` se `valorUSD < 100`; 2% se `valorUSD ≥ 100`.
4. **RN04 — Tributo didático:** 10% do `valorConvertido` (percentual fictício).
5. **RN05 — Valor final:** `valorFinalBRL = valorConvertido + taxaServico + tributoSimulado`.
6. **RN06 — Limite mensal:** a soma dos `valorFinalBRL` do cliente no mês-calendário, incluindo a nova compra, não pode passar de R$ 10.000,00. Retorna 422.
7. **RN07 — Histórico imutável:** a compra grava a cotação e os valores calculados; o histórico devolve o que foi gravado, sem recalcular.
8. **RN08 — Atomicidade:** o registro é `@Transactional` e a compra só é salva depois de cliente, cotação e limite validados. Qualquer erro antes disso não grava nada.

Exemplo do enunciado: US$ 200,00 × R$ 5,40 = R$ 1.080,00; taxa (2%) R$ 21,60; tributo (10%) R$ 108,00; total **R$ 1.209,60**.

## Validações

| Campo | Regras |
|---|---|
| `clienteId` | obrigatório, UUID (só em `POST /compras`) |
| `valorUSD` | obrigatório, maior que zero, no máximo 2 casas decimais |

## Formato dos erros

Todos os erros seguem o mesmo formato, sem stack trace:

```json
{
  "timestamp": "2026-10-30T19:40:50.432558",
  "status": 400,
  "codigo": "DADOS_INVALIDOS",
  "mensagem": "Um ou mais campos são inválidos",
  "detalhes": [
    "clienteId: O ID do cliente é obrigatório",
    "valorUSD: O valor em dólares deve ser maior que zero"
  ]
}
```

| Status | Código | Quando |
|---|---|---|
| 400 | `DADOS_INVALIDOS` | campo inválido no `@Valid` |
| 400 | `CORPO_INVALIDO` | JSON malformado ou com tipo errado (ex.: `clienteId` que não é UUID) |
| 400 | `PARAMETRO_INVALIDO` | `{id}` da rota que não é UUID |
| 404 | `CLIENTE_NAO_ENCONTRADO` | cliente inexistente |
| 422 | `LIMITE_MENSAL_EXCEDIDO` | compra estoura o limite do mês |
| 422 | `COTACAO_INVALIDA` | resposta da AwesomeAPI inválida |
| 503 | `COTACAO_INDISPONIVEL` | AwesomeAPI fora do ar ou com erro |
| 504 | `COTACAO_TIMEOUT` | AwesomeAPI não respondeu a tempo |
| 500 | `ERRO_INTERNO` | erro inesperado (detalhes só no log) |
