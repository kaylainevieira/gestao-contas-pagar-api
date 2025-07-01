package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/obter_valor_total_pago_por_periodo_test.sql"})
class ObterValorTotalPagoPorPeriodoUseCaseTest {

    @Autowired
    private ObterValorTotalPagoPorPeriodoUseCase obterValorTotalPagoPorPeriodoUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve obter o valor total pago para um período com sucesso")
    void obterValorTotalPagoPorPeriodoUseCase_01() {
        var dataInicio = LocalDate.of(2025, 7, 1);
        var dataFim = LocalDate.of(2025, 7, 31);

        var totalPago = obterValorTotalPagoPorPeriodoUseCase.execute(dataInicio, dataFim);

        assertThat(totalPago).isNotNull();
        assertThat(totalPago).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Deve retornar zero se não houver pagamentos no período")
    void obterValorTotalPagoPorPeriodoUseCase_02() {
        var dataInicio = LocalDate.of(2026, 1, 1);
        var dataFim = LocalDate.of(2026, 1, 31);

        var totalPago = obterValorTotalPagoPorPeriodoUseCase.execute(dataInicio, dataFim);

        assertThat(totalPago).isNotNull();
        assertThat(totalPago).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lançar ContaPagarRegraDeNegocioException se data de início for nula")
    void obterValorTotalPagoPorPeriodoUseCase_03() {
        var dataFim = LocalDate.of(2025, 7, 31);

        var thrown = assertThrows(ContaPagarRegraDeNegocioException.class, () ->
                obterValorTotalPagoPorPeriodoUseCase.execute(null, dataFim));

        assertThat(thrown.getMessage()).contains("As datas de início e fim do pagamento são obrigatórias");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarRegraDeNegocioException se data de fim for nula")
    void obterValorTotalPagoPorPeriodoUseCase_04() {
        var dataInicio = LocalDate.of(2025, 7, 1);

        var thrown = assertThrows(ContaPagarRegraDeNegocioException.class, () ->
                obterValorTotalPagoPorPeriodoUseCase.execute(dataInicio, null));

        assertThat(thrown.getMessage()).contains("As datas de início e fim do pagamento são obrigatórias");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarRegraDeNegocioException se data de início for posterior à data de fim")
    void obterValorTotalPagoPorPeriodoUseCase_05() {
        var dataInicio = LocalDate.of(2025, 7, 31);
        var dataFim = LocalDate.of(2025, 7, 1);

        var thrown = assertThrows(ContaPagarRegraDeNegocioException.class, () ->
                obterValorTotalPagoPorPeriodoUseCase.execute(dataInicio, dataFim));

        assertThat(thrown.getMessage()).contains("A data de início do pagamento não pode ser posterior à data de fim do pagamento.");
    }
}
