package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IObterValorTotalPagoPorPeriodoUseCase {

    BigDecimal execute(LocalDate dataPagamentoInicio, LocalDate dataPagamentoFim);
}
