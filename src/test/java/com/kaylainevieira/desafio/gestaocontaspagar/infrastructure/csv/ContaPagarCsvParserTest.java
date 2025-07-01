package com.kaylainevieira.desafio.gestaocontaspagar.infrastructure.csv;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarCsvDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarCsvException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ContaPagarCsvParserTest {

    @InjectMocks
    private ContaPagarCsvParser contaPagarCsvParser;

    @Test
    @DisplayName("Deve parsear um arquivo CSV válido com sucesso")
    void parse_01() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta de Telefone,150.75,2025-07-01,,PENDENTE\n" +
                "Parcela do Carro,300.00,2025-07-10,2025-07-05,PAGA\n" +
                "Aluguel Apartamento,1200.00,2025-07-20,,VENCIDA\n" +
                "Assinatura Internet,50.00,2025-07-25,2025-07-25,PAGA\n" +
                "Manutenção Predial,75.00,2025-08-01,,";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var result = contaPagarCsvParser.parse(inputStream);

        assertThat(result).isNotNull().hasSize(5);
        assertThat(result)
                .extracting(ContaPagarCsvDTO::getDescricao, ContaPagarCsvDTO::getValor, ContaPagarCsvDTO::getSituacao, ContaPagarCsvDTO::getDataPagamento)
                .containsExactlyInAnyOrder(
                        tuple("Conta de Telefone", new BigDecimal("150.75"), SituacaoConta.PENDENTE, null),
                        tuple("Parcela do Carro", new BigDecimal("300.00"), SituacaoConta.PAGA, LocalDate.of(2025, 7, 5)),
                        tuple("Aluguel Apartamento", new BigDecimal("1200.00"), SituacaoConta.VENCIDA, null),
                        tuple("Assinatura Internet", new BigDecimal("50.00"), SituacaoConta.PAGA, LocalDate.of(2025, 7, 25)),
                        tuple("Manutenção Predial", new BigDecimal("75.00"), SituacaoConta.PENDENTE, null) // Situação PENDENTE e dataPagamento null por default
                );
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com data de vencimento inválida")
    void parse_02() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Inválida,100.00,DATA_INVALIDA,,PENDENTE";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("Erro de formato de data na linha 2 do CSV. Verifique 'data_vencimento'");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com valor inválido")
    void parse_03() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Inválida,ABC,2025-01-01,,PENDENTE";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("Erro de formato de valor na linha 2 do CSV. Verifique 'valor'.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com descrição vazia")
    void parse_04() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                ",100.00,2025-01-01,,PENDENTE";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("Descrição não pode ser vazia.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com valor não positivo")
    void parse_05() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Negativa,-10.00,2025-01-01,,PENDENTE";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("Valor deve ser positivo.");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com situação inválida")
    void parse_06() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Situação Inválida,100.00,2025-01-01,,INVALIDO";

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("No enum constant com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta.INVALIDO");
    }

    @Test
    @DisplayName("Deve lançar ContaPagarCsvException para CSV com inconsistência dataPagamento e situacao")
    void parse_07() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Inconsistente,100.00,2025-07-01,2025-06-30,PENDENTE"; // Data de pag. mas status PENDENTE

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var thrown = assertThrows(ContaPagarCsvException.class, () ->
                contaPagarCsvParser.parse(inputStream));

        assertThat(thrown.getMessage()).contains("Inconsistência: Conta 'Conta Inconsistente' tem data de pagamento mas situação 'PENDENTE'. Esperado 'PAGA'.");
    }

    @Test
    @DisplayName("Deve preencher dataPagamento com NOW() se situacao for PAGA e dataPagamento for nula")
    void parse_08() throws IOException {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Conta Auto Paga,100.00,2025-07-01,,PAGA"; // Situação PAGA, mas data_pagamento vazia

        var inputStream = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)).getInputStream();

        var result = contaPagarCsvParser.parse(inputStream);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getSituacao()).isEqualTo(SituacaoConta.PAGA);
        assertThat(result.get(0).getDataPagamento()).isEqualTo(LocalDate.now()); // Deve ser a data de hoje
    }
}
