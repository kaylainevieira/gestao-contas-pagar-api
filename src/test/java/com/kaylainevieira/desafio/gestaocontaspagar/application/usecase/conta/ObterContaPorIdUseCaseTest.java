package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/obter_conta_por_id_test.sql"})
class ObterContaPorIdUseCaseTest {

    private static final UUID ID_CONTA_EXISTENTE = UUID.fromString("b0000000-0000-0000-0000-000000000001");

    @Autowired
    private ObterContaPorIdUseCase obterContaPorIdUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve obter uma conta existente por ID com sucesso")
    void obterContaPorIdUseCase_01() {
        var result = obterContaPorIdUseCase.execute(ID_CONTA_EXISTENTE);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID_CONTA_EXISTENTE);
        assertThat(result.getDescricao()).isEqualTo("Conta para Obter por ID");
        assertThat(result.getValor()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(result.getSituacao()).isEqualTo(SituacaoConta.PENDENTE);
    }

    @Test
    @DisplayName("Deve lançar ContaPagarNaoEncontradaException se a conta não for encontrada")
    void obterContaPorIdUseCase_02() {
        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                obterContaPorIdUseCase.execute(UUID.randomUUID()));
    }
}
