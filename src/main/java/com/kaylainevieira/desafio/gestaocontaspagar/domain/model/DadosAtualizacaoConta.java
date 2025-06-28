package com.kaylainevieira.desafio.gestaocontaspagar.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class DadosAtualizacaoConta {
    LocalDate dataVencimento;
    BigDecimal valor;
    String descricao;
}