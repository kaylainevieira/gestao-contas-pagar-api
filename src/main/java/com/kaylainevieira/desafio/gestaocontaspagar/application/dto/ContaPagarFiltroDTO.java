package com.kaylainevieira.desafio.gestaocontaspagar.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class ContaPagarFiltroDTO {

    @NotNull(message = "A data de vencimento inicial é obrigatória para o filtro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataVencimentoInicio;

    @NotNull(message = "A data de vencimento final é obrigatória para o filtro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataVencimentoFim;

    @NotBlank(message = "A descrição é obrigatória para o filtro e não pode ser vazia.")
    private String descricao;
}