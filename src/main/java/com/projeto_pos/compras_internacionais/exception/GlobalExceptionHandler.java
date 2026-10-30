package com.projeto_pos.compras_internacionais.exception;

import com.projeto_pos.compras_internacionais.dto.ErroResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErroResponse> handleApi(ApiException ex) {
        log.warn("{}: {}", ex.getCodigo(), ex.getMessage());
        return build(ex.getStatus(), ex.getCodigo(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "DADOS_INVALIDOS", "Um ou mais campos são inválidos", detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleBodyIlegivel(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "CORPO_INVALIDO", "Corpo da requisição ausente ou malformado", List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO",
                "Valor inválido para o parâmetro '" + ex.getName() + "'", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(Exception ex) {
        // Spring MVC errors (404 route, 405 method, 415 media type...) carry their own status
        if (ex instanceof ErrorResponse springErro) {
            HttpStatusCode status = springErro.getStatusCode();
            String mensagem = Objects.requireNonNullElse(springErro.getBody().getDetail(), "Requisição inválida");
            return build(status, "ERRO_REQUISICAO", mensagem, List.of());
        }
        log.error("Erro inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", "Erro interno inesperado", List.of());
    }

    private ResponseEntity<ErroResponse> build(HttpStatusCode status, String codigo, String mensagem, List<String> detalhes) {
        ErroResponse body = new ErroResponse(LocalDateTime.now(), status.value(), codigo, mensagem, detalhes);
        return ResponseEntity.status(status).body(body);
    }
}
