package com.kaylainevieira.desafio.gestaocontaspagar.presentation.exception;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarCsvException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(
                ErroResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .message("Erro de validação nos campos da requisição.")
                        .details(errors.toString())
                        .path(request.getDescription(false))
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ContaPagarNaoEncontradaException.class)
    public ResponseEntity<Object> handleNotFoundException(ContaPagarNaoEncontradaException ex, WebRequest request) {
        return new ResponseEntity<>(
                ErroResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Not Found")
                        .message(ex.getMessage())
                        .path(request.getDescription(false))
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ContaPagarRegraDeNegocioException.class)
    public ResponseEntity<Object> handleContaPagarRegraDeNegocioException(ContaPagarRegraDeNegocioException ex, WebRequest request) {
        return new ResponseEntity<>(
                ErroResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error("Conflict")
                        .message(ex.getMessage())
                        .path(request.getDescription(false))
                        .build(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ContaPagarCsvException.class)
    public ResponseEntity<Object> handleContaPagarCsvException(ContaPagarCsvException ex, WebRequest request) {
        return new ResponseEntity<>(
                ErroResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .message("Erro no arquivo CSV fornecido: " + ex.getMessage())
                        .path(request.getDescription(false))
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                ErroResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .message("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.")
                        .details(ex.getMessage())
                        .path(request.getDescription(false))
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
