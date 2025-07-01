package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/atualizar_conta_test.sql"})
class AtualizarContaUseCaseTest {

    private static final UUID ID_CONTA_PARA_ATUALIZAR = UUID.fromString("1a2b3c4d-e5f6-7890-1234-567890abcdef");
    private static final UUID ID_CONTA_PAGA_NAO_ATUALIZAVEL = UUID.fromString("f1e2d3c4-b5a6-7890-1234-567890abcdef");
    private static final UUID ID_CONTA_NAO_EXISTENTE = UUID.fromString("98765432-10ab-cdef-0123-456789abcdef");

    @Autowired
    private AtualizarContaUseCase atualizarContaUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve atualizar os dados de uma conta PENDENTE com sucesso")
    void atualizarContaUseCase_01() {
        var input = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 7, 10))
                .valor(new BigDecimal("550.00"))
                .descricao("Nova Descricao Atualizada")
                .build();

        var result = atualizarContaUseCase.execute(ID_CONTA_PARA_ATUALIZAR, input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID_CONTA_PARA_ATUALIZAR);
        assertThat(result.getDescricao()).isEqualTo("Nova Descricao Atualizada");
        assertThat(result.getValor()).isEqualByComparingTo(new BigDecimal("550.00"));
        assertThat(result.getDataVencimento()).isEqualTo(LocalDate.of(2025, 7, 10));

        var contaNoBanco = contaPagarRepository.findById(ID_CONTA_PARA_ATUALIZAR).orElse(null);
        assertThat(contaNoBanco).isNotNull();
        assertThat(contaNoBanco.getDescricao()).isEqualTo("Nova Descricao Atualizada");
        assertThat(contaNoBanco.getValor()).isEqualByComparingTo(new BigDecimal("550.00"));
        assertThat(contaNoBanco.getDataVencimento()).isEqualTo(LocalDate.of(2025, 7, 10));
    }

    @Test
    @DisplayName("Deve lançar ContaPagarNaoEncontradaException se a conta não for encontrada")
    void atualizarContaUseCase_02() {
        var input = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 7, 10))
                .valor(new BigDecimal("550.00"))
                .descricao("Descricao Inexistente")
                .build();

        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                atualizarContaUseCase.execute(ID_CONTA_NAO_EXISTENTE, input));
    }

    @Test
    @DisplayName("Deve lançar ContaPagarRegraDeNegocioException se tentar atualizar uma conta PAGA")
    void atualizarContaUseCase_03() {
        var input = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 7, 10))
                .valor(new BigDecimal("550.00"))
                .descricao("Tentativa de Atualizacao")
                .build();

        var thrown = assertThrows(ContaPagarRegraDeNegocioException.class, () ->
                atualizarContaUseCase.execute(ID_CONTA_PAGA_NAO_ATUALIZAVEL, input));

        assertThat(thrown.getMessage()).contains("Não é possível atualizar os dados de uma conta que já foi paga.");
    }
}
