package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.AlterarSituacaoRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/alterar_situacao_conta_test.sql"})
class AlterarSituacaoContaUseCaseTest {

    private static final UUID ID_CONTA_PENDENTE = UUID.fromString("c264a982-f19b-4394-bb9e-319f6f3933c0");
    private static final UUID ID_CONTA_PAGA = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private static final UUID ID_CONTA_NAO_EXISTENTE = UUID.fromString("400f0e26-5c2c-4444-ac74-b02040b77263");

    @Autowired
    private AlterarSituacaoContaUseCase alterarSituacaoContaUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve alterar a situação da conta com sucesso")
    void alterarSituacaoContaUseCase_01() {
        var input = AlterarSituacaoRequestDTO.builder()
                .novaSituacao(SituacaoConta.PAGA)
                .build();
        var result = alterarSituacaoContaUseCase.execute(ID_CONTA_PENDENTE, input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ID_CONTA_PENDENTE);
        assertThat(result.getSituacao()).isEqualTo(SituacaoConta.PAGA);
        assertThat(result.getDataPagamento()).isNotNull();

        var contaNoBanco = contaPagarRepository.findById(ID_CONTA_PENDENTE).orElse(null);
        assertThat(contaNoBanco).isNotNull();
        assertThat(contaNoBanco.getSituacao()).isEqualTo(SituacaoConta.PAGA);
        assertThat(contaNoBanco.getDataPagamento()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar ContaPagarNaoEncontradaException se a conta não for encontrada")
    void alterarSituacaoContaUseCase_02() {
        var input = AlterarSituacaoRequestDTO.builder()
                .novaSituacao(SituacaoConta.PAGA)
                .build();

        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                alterarSituacaoContaUseCase.execute(ID_CONTA_NAO_EXISTENTE, input));
    }

    @Test
    @DisplayName("Deve lançar ContaPagarRegraDeNegocioException se a entidade lançar IllegalStateException")
    void alterarSituacaoContaUseCase_03() {
        var input = AlterarSituacaoRequestDTO.builder()
                .novaSituacao(SituacaoConta.PAGA)
                .build();
        var thrown = assertThrows(ContaPagarRegraDeNegocioException.class, () ->
                alterarSituacaoContaUseCase.execute(ID_CONTA_PAGA, input));

        assertThat(thrown.getMessage()).contains("Não é possível pagar uma conta com a situação: PAGA");
    }
}
