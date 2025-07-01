package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CadastrarContaUseCaseTest {

    @Autowired
    private CadastrarContaUseCase cadastrarContaUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve cadastrar uma nova conta com status PENDENTE com sucesso")
    void cadastrarContaUseCase_01() {
        var input = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 12, 25))
                .valor(new BigDecimal("123.45"))
                .descricao("Conta de Natal")
                .build();

        var result = cadastrarContaUseCase.execute(input);

        assertThat(result).isNotNull();
        assertNotNull(result.getId());
        assertThat(result.getDataVencimento()).isEqualTo(input.getDataVencimento());
        assertThat(result.getValor()).isEqualByComparingTo(input.getValor());
        assertThat(result.getDescricao()).isEqualTo(input.getDescricao());
        assertThat(result.getSituacao()).isEqualTo(SituacaoConta.PENDENTE);
        assertNotNull(result.getDataCadastro());

        var contaNoBanco = contaPagarRepository.findById(result.getId()).orElse(null);
        assertThat(contaNoBanco).isNotNull();
        assertThat(contaNoBanco.getDescricao()).isEqualTo(input.getDescricao());
        assertThat(contaNoBanco.getSituacao()).isEqualTo(SituacaoConta.PENDENTE);
    }
}
