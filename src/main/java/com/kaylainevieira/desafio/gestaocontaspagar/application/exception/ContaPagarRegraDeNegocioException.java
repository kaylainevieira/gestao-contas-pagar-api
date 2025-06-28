package com.kaylainevieira.desafio.gestaocontaspagar.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContaPagarRegraDeNegocioException extends RuntimeException {

    public ContaPagarRegraDeNegocioException(String message) {
        super(message);
    }
}
