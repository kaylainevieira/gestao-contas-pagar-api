package com.kaylainevieira.desafio.gestaocontaspagar.application.dto;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ContaPagarCsvDTO {
    @NotNull(message = "Data de vencimento no CSV é obrigatória.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataVencimento;

    @NotNull(message = "Valor no CSV é obrigatório.")
    @DecimalMin(value = "0.01", message = "Valor no CSV deve ser maior que zero.")
    private BigDecimal valor;

    @NotBlank(message = "Descrição no CSV é obrigatória e não pode ser vazia.")
    @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres.")
    private String descricao;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPagamento;

    @Nullable
    private SituacaoConta situacao;
}
