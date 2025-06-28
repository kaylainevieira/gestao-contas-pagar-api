package com.kaylainevieira.desafio.gestaocontaspagar.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContaPagarNaoEncontradaException extends RuntimeException {

    public ContaPagarNaoEncontradaException(String message) {
        super(message);
    }
}