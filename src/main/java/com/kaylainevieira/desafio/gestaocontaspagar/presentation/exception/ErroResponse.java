package com.kaylainevieira.desafio.gestaocontaspagar.presentation.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErroResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String details;
    private String path;
}
