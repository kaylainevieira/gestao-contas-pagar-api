package com.kaylainevieira.desafio.gestaocontaspagar.application.query;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
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
@Sql({"classpath:db/sql/application/query/conta_pagar_query_service_test.sql"})
class ContaPagarQueryServiceTest {

    private static final UUID ID_CONTA_EXISTENTE = UUID.fromString("7b3e1c2a-8d9f-4e0c-9a1b-2c3d4e5f6789");

    @Autowired
    private ContaPagarQueryService contaPagarQueryService;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve buscar uma conta existente por ID com sucesso")
    void buscarContaPagarPorIdTest_01() {
        ContaPagar result = contaPagarQueryService.buscarContaPagarPorId(ID_CONTA_EXISTENTE);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID_CONTA_EXISTENTE);
        assertThat(result.getDescricao()).isEqualTo("Conta para Query Service Teste");
        assertThat(result.getValor()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getSituacao()).isEqualTo(SituacaoConta.PENDENTE);
    }

    @Test
    @DisplayName("Deve lançar ContaPagarNaoEncontradaException se a conta não for encontrada")
    void buscarContaPagarPorIdTest_02() {
        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                contaPagarQueryService.buscarContaPagarPorId(UUID.randomUUID()));
    }
}
