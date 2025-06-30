package com.kaylainevieira.desafio.gestaocontaspagar.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContaPagarCsvException extends RuntimeException {

    public ContaPagarCsvException(String message) {
        super(message);
    }

    public ContaPagarCsvException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContaPagarCsvException(String messageFormat, int lineNumber, Throwable cause) {
        super(String.format(messageFormat, lineNumber), cause);
    }

    public ContaPagarCsvException(String messageFormat, int lineNumber, String detail, Throwable cause) {
        super(String.format(messageFormat, lineNumber, detail), cause);
    }
}
